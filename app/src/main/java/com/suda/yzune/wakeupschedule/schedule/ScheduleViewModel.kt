package com.suda.yzune.wakeupschedule.schedule

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.*
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import java.io.File
import java.util.*
import kotlin.concurrent.thread

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private val tableDao = dataBase.tableDao()
    private val widgetDao = dataBase.appWidgetDao()
    private val timeTableDao = dataBase.timeTableDao()
    private val timeDao = dataBase.timeDetailDao()
    private val json = PreferenceUtils.getStringFromSP(application, "course", "")!!

    lateinit var tableData: LiveData<TableBean>
    lateinit var timeData: LiveData<List<TimeDetailBean>>
    var selectedWeek = 1
    val currentWeek = MutableLiveData<Int>()
    val marTop = application.resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
    var itemHeight = 0
    var alphaStr = ""
    val tableSelectList = arrayListOf<TableSelectBean>()
    val allCourseList = Array(7) { MutableLiveData<List<CourseBean>>() }
    val exportImportInfo = MutableLiveData<String>()
    val addBlankTableInfo = MutableLiveData<String>()
    val daysArray = arrayOf("日", "一", "二", "三", "四", "五", "六", "日")

    fun initTableSelectList(): LiveData<List<TableSelectBean>> {
        return tableDao.getTableSelectList()
    }

    fun addBlankTable(tableName: String) {
        thread(name = "AddBlankTableThread") {
            try {
                tableDao.insertTable(TableBean(id = 0, tableName = tableName))
                addBlankTableInfo.postValue("OK")
            } catch (e: SQLiteConstraintException) {
                addBlankTableInfo.postValue("error")
            }
        }
    }

    fun changeDefaultTable(id: Int) {
        if (tableData.value?.id == null) return
        tableDao.resetOldDefaultTable(tableData.value?.id!!)
        tableDao.setNewDefaultTable(id)
    }

    fun initViewData(): LiveData<TableBean> {
        tableData = tableDao.getDefaultTable()
        return tableData
    }

    fun initTimeData(timeTableId: Int): LiveData<List<TimeDetailBean>> {
        timeData = timeDao.getTimeList(timeTableId)
        return timeData
    }

    fun getScheduleWidgetIds(): LiveData<List<AppWidgetBean>> {
        return widgetDao.getWidgetsByBaseType(0)
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

    fun exportData(currentDir: String) {
        thread(name = "ExportDataThread") {
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
            strBuilder.append(gson.toJson(timeTableDao.getTimeTableInThread(tableData.value!!.timeTable)))
            strBuilder.append("\n${gson.toJson(timeDao.getTimeListInThread(tableData.value!!.timeTable))}")
            strBuilder.append("\n${gson.toJson(tableData.value!!)}")
            strBuilder.append("\n${gson.toJson(baseDao.getCourseBaseBeanOfTableInThread(tableData.value!!.id))}")
            strBuilder.append("\n${gson.toJson(detailDao.getDetailOfTableInThread(tableData.value!!.id))}")
            val file = File(myDir, "${tableData.value!!.tableName}${Calendar.getInstance().timeInMillis}.wakeup_schedule")
            file.writeText(strBuilder.toString())
            exportImportInfo.postValue(file.path)
        }
    }
}