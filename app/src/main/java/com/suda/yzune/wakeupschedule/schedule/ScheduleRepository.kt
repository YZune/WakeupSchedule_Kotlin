package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.*
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils.courseBean2DetailBean
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import kotlin.concurrent.thread

class ScheduleRepository(context: Context) {

    private val dataBase = AppDatabase.getDatabase(context)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private val widgetDao = dataBase.appWidgetDao()
    private val timeDao = dataBase.timeDetailDao()
    private val json = PreferenceUtils.getStringFromSP(context, "course", "")
    private val clipboardImportInfo = MutableLiveData<String>()
    private var clipboardCourseList: List<CourseBean>? = null

    fun getClipboardImportInfo(): LiveData<String> {
        return clipboardImportInfo
    }

    fun tranClipboardStr(str: String) {
        val gson = Gson()
        try {
            clipboardCourseList = gson.fromJson<List<CourseBean>>(str.substring(15), object : TypeToken<List<CourseBean>>() {}.type)
        } catch (e: JsonSyntaxException) {
            clipboardImportInfo.value = "解析异常"
            clipboardCourseList = null
        }
    }

    fun tranClipboardCourseList(isLover: Boolean) {
        if (clipboardCourseList == null) return
        val baseList = arrayListOf<CourseBaseBean>()
        val detailList = arrayListOf<CourseDetailBean>()
        clipboardCourseList!!.forEach {
            baseList.add(CourseUtils.courseBean2BaseBean(it).apply {
                if (isLover) {
                    this.tableName = "lover"
                }
            })

            detailList.add(CourseUtils.courseBean2DetailBean(it).apply {
                if (isLover) {
                    this.tableName = "lover"
                }
            })
        }

        write2DB(baseList, detailList, isLover)
    }

    private fun write2DB(baseList: List<CourseBaseBean>, detailList: List<CourseDetailBean>, isLover: Boolean) {
        thread(name = "ImportClipboardDataThread") {
            if (isLover) {
                baseDao.removeCourseData("lover")
            } else {
                baseDao.removeCourseData("")
            }
            try {
                baseDao.insertList(baseList)
                detailDao.insertList(detailList)
                clipboardImportInfo.postValue(if (isLover) "love" else "ok")
                Log.d("数据库", "插入")
            } catch (e: SQLiteConstraintException) {
                Log.d("数据库", "插入异常$e")
                clipboardImportInfo.postValue("插入异常")
                //insertResponse.value = "error"
            }
        }
    }

    private fun oldBean2CourseBean(list: List<CourseOldBean>, tableName: String, context: Context) {
        val baseList = arrayListOf<CourseBaseBean>()
        val detailList = arrayListOf<CourseDetailBean>()
        var id = 0
        for (oldBean in list) {
            val flag = CourseUtils.isContainName(baseList, oldBean.name)
            if (flag == -1) {
                baseList.add(CourseBaseBean(id, oldBean.name, "", tableName))
                detailList.add(CourseDetailBean(
                        id = id, room = oldBean.room,
                        teacher = oldBean.teach, day = oldBean.day,
                        step = oldBean.step, startWeek = oldBean.startWeek, endWeek = oldBean.endWeek,
                        type = oldBean.isOdd, startNode = oldBean.start,
                        tableName = tableName
                ))
                id++
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = oldBean.room,
                        teacher = oldBean.teach, day = oldBean.day,
                        step = oldBean.step, startWeek = oldBean.startWeek, endWeek = oldBean.endWeek,
                        type = oldBean.isOdd, startNode = oldBean.start,
                        tableName = tableName
                ))
            }
        }

        thread(name = "TransformOldDataThread") {
            try {
                detailList.forEach {
                    println(it.toString())
                }
                baseDao.insertList(baseList)
                detailDao.insertList(detailList)
                //insertResponse.value = "ok"
                Log.d("数据库", "插入")
                PreferenceUtils.saveStringToSP(context.applicationContext, "course", "")
            } catch (e: SQLiteConstraintException) {
                Log.d("数据库", "插入异常$e")
                //insertResponse.value = "error"
            }
        }
    }

    fun removeCourseData() {
        baseDao.removeCourseData("")
    }

    fun updateFromOldVer(context: Context) {
        if (json != "") {
            val gson = Gson()
            val list = gson.fromJson<List<CourseOldBean>>(json, object : TypeToken<List<CourseOldBean>>() {
            }.type)

            oldBean2CourseBean(list, "", context)
        }
    }

    fun getTimeDetailLiveList(): LiveData<List<TimeDetailBean>> {
        return timeDao.getTimeList()
    }

    fun getSummerTimeLiveList(): LiveData<List<TimeDetailBean>> {
        return timeDao.getSummerTimeList()
    }

    fun getRawCourseByDay(day: Int, tableName: String = ""): LiveData<List<CourseBean>> {
        return baseDao.getCourseByDay(day, tableName)
    }

    fun getScheduleWidgetIds(): LiveData<List<Int>> {
        return widgetDao.getLiveIdsByTypes(0, 0)
    }

    fun getCourse(): LiveData<List<CourseBean>> {
        return baseDao.getCourse()
    }

    fun getCourseByDay(raw: List<CourseBean>): LiveData<List<CourseBean>> {
        val result = MutableLiveData<List<CourseBean>>()
        val list = ArrayList<CourseBean>()
        val changeIndex = arrayListOf<Int>()
        result.value = list
        var i = 0
        while (i < raw.size) {
            if (i != raw.size - 1 && raw[i].id == raw[i + 1].id
                    && raw[i].room == raw[i + 1].room
                    && raw[i].startNode + raw[i].step == raw[i + 1].startNode) {
                raw[i].step += raw[i + 1].step
                list.add(raw[i])
                changeIndex.add(i)
                i += 2
            } else {
                list.add(raw[i])
                i++
            }
        }
        result.value = list
        thread(name = "MakeTogetherThread") {
            for (index in changeIndex) {
                detailDao.updateCourseDetail(courseBean2DetailBean(raw[index]))
                detailDao.deleteCourseDetail(courseBean2DetailBean(raw[index + 1]))
            }
        }
        return result
    }

    fun deleteCourseBean(courseBean: CourseBean) {
        thread(name = "DeleteCourseBeanThread") {
            detailDao.deleteCourseDetail(courseBean2DetailBean(courseBean))
        }
    }

    fun deleteCourseBaseBean(id: Int, tableName: String) {
        thread(name = "DeleteCourseBaseBeanThread") {
            baseDao.deleteCourseBaseBean(id, tableName)
        }
    }

    fun updateCourseBaseBean(course: CourseBaseBean) {
        thread(name = "UpdateCourseBaseBeanThread") {
            baseDao.updateCourseBaseBean(course)
        }
    }
}