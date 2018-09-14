package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.google.gson.Gson
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


    fun write2DB(baseList: List<CourseBaseBean>, detailList: List<CourseDetailBean>, tableName: String = "", clipboardImportInfo: MutableLiveData<String>) {
        thread(name = "ImportClipboardDataThread") {
            baseDao.removeCourseData(tableName)
            try {
                baseDao.insertList(baseList)
                detailDao.insertList(detailList)
                clipboardImportInfo.postValue("ok")
            } catch (e: SQLiteConstraintException) {
                clipboardImportInfo.postValue("插入异常")
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
                Log.d("数据库", "插入")
                PreferenceUtils.saveStringToSP(context.applicationContext, "course", "")
            } catch (e: SQLiteConstraintException) {
                Log.d("数据库", "插入异常$e")
            }
        }
    }

    fun updateFromOldVer(context: Context, json: String) {
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