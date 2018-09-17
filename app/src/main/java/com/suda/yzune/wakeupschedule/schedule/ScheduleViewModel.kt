package com.suda.yzune.wakeupschedule.schedule

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.*
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import kotlin.concurrent.thread

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private val tableDao = dataBase.tableDao()
    private val widgetDao = dataBase.appWidgetDao()
    private val timeDao = dataBase.timeDetailDao()
    private val json = PreferenceUtils.getStringFromSP(application, "course", "")!!

    lateinit var tableData: LiveData<TableBean>
    var selectedWeek = 1
    val currentWeek = MutableLiveData<Int>()
    val marTop = application.resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
    var itemHeight = 0
    val timeList = arrayListOf<TimeDetailBean>()
    val allCourseList = Array(7) { MutableLiveData<List<CourseBean>>() }
    val clipboardImportInfo = MutableLiveData<String>()
    var clipboardCourseList: List<CourseBean>? = null
    val daysArray = arrayOf("日", "一", "二", "三", "四", "五", "六", "日")

    fun initViewData(tableId: Int? = null): LiveData<TableBean> {
        tableData = if (tableId == null) {
            tableDao.getDefaultTable()
        } else {
            tableDao.getTableById(tableId)
        }
        return tableData
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

    fun getScheduleWidgetIds(tableName: String): LiveData<List<Int>> {
        return widgetDao.getIdsOfWeekTypeOfTable(tableName)
    }

    fun getRawCourseByDay(day: Int, tableId: Int = 0): LiveData<List<CourseBean>> {
        return baseDao.getCourseByDayOfTable(day, tableId)
    }

    fun getCourse(tableId: Int): LiveData<List<CourseBean>> {
        return baseDao.getCourseOfTable(tableId)
    }

    fun deleteCourseBean(courseBean: CourseBean) {
        thread(name = "DeleteCourseBeanThread") {
            detailDao.deleteCourseDetail(CourseUtils.courseBean2DetailBean(courseBean))
        }
    }

    fun deleteCourseBaseBean(id: Int, tableId: Int = 0) {
        thread(name = "DeleteCourseBaseBeanThread") {
            baseDao.deleteCourseBaseBeanOfTable(id, tableId)
        }
    }

    fun updateCourseBaseBean(course: CourseBean) {
        thread(name = "UpdateCourseBaseBeanThread") {
            baseDao.updateCourseBaseBean(CourseUtils.courseBean2BaseBean(course))
        }
    }

    fun updateFromOldVer() {
        if (json != "") {
            val gson = Gson()
            val list = gson.fromJson<List<CourseOldBean>>(json, object : TypeToken<List<CourseOldBean>>() {
            }.type)
            oldBean2CourseBean(list, 0)
        }
    }

    private fun oldBean2CourseBean(list: List<CourseOldBean>, tableId: Int) {
        val baseList = arrayListOf<CourseBaseBean>()
        val detailList = arrayListOf<CourseDetailBean>()
        var id = 0
        for (oldBean in list) {
            val flag = CourseUtils.isContainName(baseList, oldBean.name)
            if (flag == -1) {
                baseList.add(CourseBaseBean(id, oldBean.name, "", tableId))
                detailList.add(CourseDetailBean(
                        id = id, room = oldBean.room,
                        teacher = oldBean.teach, day = oldBean.day,
                        step = oldBean.step, startWeek = oldBean.startWeek, endWeek = oldBean.endWeek,
                        type = oldBean.isOdd, startNode = oldBean.start,
                        tableId = tableId
                ))
                id++
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = oldBean.room,
                        teacher = oldBean.teach, day = oldBean.day,
                        step = oldBean.step, startWeek = oldBean.startWeek, endWeek = oldBean.endWeek,
                        type = oldBean.isOdd, startNode = oldBean.start,
                        tableId = tableId
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
                PreferenceUtils.remove(getApplication(), "course")
            } catch (e: SQLiteConstraintException) {
                Log.d("数据库", "插入异常$e")
            }
        }
    }

    private fun writeCourse2DB(baseList: List<CourseBaseBean>, detailList: List<CourseDetailBean>, tableId: Int = 0, clipboardImportInfo: MutableLiveData<String>) {
        thread(name = "ImportClipboardDataThread") {
            baseDao.removeCourseBaseBeanOfTable(tableId)
            try {
                baseDao.insertList(baseList)
                detailDao.insertList(detailList)
                clipboardImportInfo.postValue("ok")
            } catch (e: SQLiteConstraintException) {
                clipboardImportInfo.postValue("插入异常")
            }
        }
    }
}