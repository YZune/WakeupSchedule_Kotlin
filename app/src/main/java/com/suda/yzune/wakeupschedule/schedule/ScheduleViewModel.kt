package com.suda.yzune.wakeupschedule.schedule

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import biweekly.Biweekly
import biweekly.ICalVersion
import biweekly.ICalendar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.*
import com.suda.yzune.wakeupschedule.schedule_import.SchoolListBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.ICalUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private val tableDao = dataBase.tableDao()
    private val widgetDao = dataBase.appWidgetDao()
    private val timeTableDao = dataBase.timeTableDao()
    private val timeDao = dataBase.timeDetailDao()

    lateinit var table: TableBean
    lateinit var timeList: List<TimeDetailBean>
    var selectedWeek = 1
    val marTop = application.resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
    var itemHeight = 0
    var statusBarMargin = 0
    var alphaStr = ""
    val tableSelectList = arrayListOf<TableSelectBean>()
    val allCourseList = Array(7) { MutableLiveData<List<CourseBean>>() }
    val daysArray = arrayOf("日", "一", "二", "三", "四", "五", "六", "日")

    fun initTableSelectList(): LiveData<List<TableSelectBean>> {
        return tableDao.getTableSelectList()
    }

    fun getImportSchoolBean(): SchoolListBean {
        val json = PreferenceUtils.getStringFromSP(getApplication(), "import_school", null)
                ?: return SchoolListBean("S", "苏州大学", "")
        val gson = Gson()
        return try {
            gson.fromJson<SchoolListBean>(json, object : TypeToken<SchoolListBean>() {}.type)
        } catch (e: Exception) {
            SchoolListBean("S", "苏州大学", "")
        }
    }

    fun getMultiCourse(week: Int, day: Int, startNode: Int): List<CourseBean> {
        return allCourseList[day - 1].value!!.filter {
            it.inWeek(week) && it.startNode == startNode
        }
    }

    suspend fun getDefaultTable(): TableBean {
        return tableDao.getDefaultTableInThread()
    }

    suspend fun getTimeList(timeTableId: Int): List<TimeDetailBean> {
        return timeDao.getTimeListInThread(timeTableId)
    }

    suspend fun addBlankTableAsync(tableName: String) {
        tableDao.insertTable(TableBean(id = 0, tableName = tableName))
    }

    suspend fun changeDefaultTable(id: Int) {
        tableDao.resetOldDefaultTable(table.id)
        tableDao.setNewDefaultTable(id)
    }

    suspend fun getScheduleWidgetIds(): List<AppWidgetBean> {
        return widgetDao.getWidgetsByBaseTypeInThread(0)
    }

    fun getRawCourseByDay(day: Int, tableId: Int): LiveData<List<CourseBean>> {
        return baseDao.getCourseByDayOfTable(day, tableId)
    }

    suspend fun deleteCourseBean(courseBean: CourseBean) {
        detailDao.deleteCourseDetail(CourseUtils.courseBean2DetailBean(courseBean))
    }

    suspend fun deleteCourseBaseBean(id: Int, tableId: Int) {
        baseDao.deleteCourseBaseBeanOfTable(id, tableId)
    }

    suspend fun updateFromOldVer(json: String) {
        val gson = Gson()
        val list = gson.fromJson<List<CourseOldBean>>(json, object : TypeToken<List<CourseOldBean>>() {
        }.type)
        val lastId = tableDao.getLastIdInThread()
        val tableId = if (lastId != null) {
            lastId + 1
        } else {
            1
        }
        oldBean2CourseBean(list, tableId)
    }

    private suspend fun oldBean2CourseBean(list: List<CourseOldBean>, tableId: Int) {
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
        baseDao.insertList(baseList)
        detailDao.insertList(detailList)
        PreferenceUtils.remove(getApplication(), "course")
    }

    suspend fun exportData(currentDir: String): String {
        val myDir = if (currentDir.endsWith(File.separator)) {
            "${currentDir}WakeUp课程表/"
        } else {
            "$currentDir/WakeUp课程表/"
        }
        val dir = File(myDir)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val gson = Gson()
        val strBuilder = StringBuilder()
        strBuilder.append(gson.toJson(timeTableDao.getTimeTableInThread(table.timeTable)))
        strBuilder.append("\n${gson.toJson(timeList)}")
        strBuilder.append("\n${gson.toJson(table)}")
        strBuilder.append("\n${gson.toJson(baseDao.getCourseBaseBeanOfTableInThread(table.id))}")
        strBuilder.append("\n${gson.toJson(detailDao.getDetailOfTableInThread(table.id))}")
        val tableName = if (table.tableName == "") {
            "我的课表"
        } else {
            table.tableName
        }
        val file = File(myDir, "$tableName${Calendar.getInstance().timeInMillis}.wakeup_schedule")
        file.writeText(strBuilder.toString())
        return file.path
    }

    suspend fun exportICS(currentDir: String): String {
        val myDir = if (currentDir.endsWith(File.separator)) {
            "${currentDir}WakeUp课程表/"
        } else {
            "$currentDir/WakeUp课程表/"
        }
        val dir = File(myDir)
        if (!dir.exists()) {
            dir.mkdir()
        }
        //val week = CourseUtils.countWeekForExport(table.startDate, table.sundayFirst)
        val ical = ICalendar()
        ical.setProductId("-//YZune//WakeUpSchedule//EN")
//        calendar.properties.add(ProdId("-//WakeUpSchedule //iCal4j 2.0//EN"))
//        calendar.properties.add(Version.VERSION_2_0)
//        calendar.properties.add(CalScale.GREGORIAN)
        val startTimeMap = ICalUtils.getClassTime(timeList, true)
        val endTimeMap = ICalUtils.getClassTime(timeList, false)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val date = sdf.parse(table.startDate)
        allCourseList.forEach {
            it.value?.forEach { course ->
                try {
                    ICalUtils.getClassEvents(ical, startTimeMap, endTimeMap, table.maxWeek, course, date)
                } catch (ignored: Exception) {

                }
            }
        }
        val warnings = ical.validate(ICalVersion.V2_0)
        Log.d("日历", warnings.toString())
        val tableName = if (table.tableName == "") {
            "我的课表"
        } else {
            table.tableName
        }
        val file = File(myDir, "日历-$tableName.ics")
        Biweekly.write(ical).go(file)
        return file.path
    }
}