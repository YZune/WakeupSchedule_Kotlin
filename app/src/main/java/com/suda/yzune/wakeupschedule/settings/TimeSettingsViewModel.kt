package com.suda.yzune.wakeupschedule.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.bean.TimeTableBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils

class TimeSettingsViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var table: TableBean

    private val dataBase = AppDatabase.getDatabase(application)
    private val timeDao = dataBase.timeDetailDao()
    private val timeTableDao = dataBase.timeTableDao()
    val timeTableList = arrayListOf<TimeTableBean>()
    val timeList = arrayListOf<TimeDetailBean>()
    val timeSelectList = arrayListOf<String>()
    val deleteInfo = MutableLiveData<String>()

    var entryPosition = 0
    var selectedId = 1

    suspend fun addNewTimeTable(name: String) {
        timeTableDao.insertTimeTable(TimeTableBean(id = 0, name = name))
    }

    suspend fun initTimeTableData(id: Int) {
        val timeList = ArrayList<TimeDetailBean>().apply {
            add(TimeDetailBean(1, "08:00", "08:50", id))
            add(TimeDetailBean(2, "09:00", "09:50", id))
            add(TimeDetailBean(3, "10:10", "11:00", id))
            add(TimeDetailBean(4, "11:10", "12:00", id))
            add(TimeDetailBean(5, "13:30", "14:20", id))
            add(TimeDetailBean(6, "14:30", "15:20", id))
            add(TimeDetailBean(7, "15:40", "16:30", id))
            add(TimeDetailBean(8, "16:40", "17:30", id))
            add(TimeDetailBean(9, "18:30", "19:20", id))
            add(TimeDetailBean(10, "19:30", "20:20", id))
            add(TimeDetailBean(11, "20:30", "21:20", id))
            add(TimeDetailBean(12, "00:00", "00:00", id))
            add(TimeDetailBean(13, "00:00", "00:00", id))
            add(TimeDetailBean(14, "00:00", "00:00", id))
            add(TimeDetailBean(15, "00:00", "00:00", id))
            add(TimeDetailBean(16, "00:00", "00:00", id))
            add(TimeDetailBean(17, "00:00", "00:00", id))
            add(TimeDetailBean(18, "00:00", "00:00", id))
            add(TimeDetailBean(19, "00:00", "00:00", id))
            add(TimeDetailBean(20, "00:00", "00:00", id))
            add(TimeDetailBean(21, "00:00", "00:00", id))
            add(TimeDetailBean(22, "00:00", "00:00", id))
            add(TimeDetailBean(23, "00:00", "00:00", id))
            add(TimeDetailBean(24, "00:00", "00:00", id))
            add(TimeDetailBean(25, "00:00", "00:00", id))
            add(TimeDetailBean(26, "00:00", "00:00", id))
            add(TimeDetailBean(27, "00:00", "00:00", id))
            add(TimeDetailBean(28, "00:00", "00:00", id))
            add(TimeDetailBean(29, "00:00", "00:00", id))
            add(TimeDetailBean(30, "00:00", "00:00", id))
        }
        timeDao.insertTimeList(timeList)
    }


    suspend fun deleteTimeTable(timeTableBean: TimeTableBean) {
        timeTableDao.deleteTimeTable(timeTableBean)
    }

    fun getTimeTableList(): LiveData<List<TimeTableBean>> {
        return timeTableDao.getTimeTableList()
    }

    fun getTimeData(id: Int): LiveData<List<TimeDetailBean>> {
        return timeDao.getTimeList(id)
    }

    suspend fun saveDetailData(tablePosition: Int) {
        timeTableDao.updateTimeTable(timeTableList[tablePosition])
        timeDao.updateTimeDetailList(timeList)
    }

    fun initTimeSelectList() {
        for (i in 6..24) {
            for (j in 0..55 step 5) {
                val h = if (i < 10) "0$i" else i.toString()
                val m = if (j < 10) "0$j" else j.toString()
                timeSelectList.add("$h:$m")
            }
        }
    }

    fun refreshEndTime(min: Int) {
        timeList.forEach {
            it.endTime = CourseUtils.calAfterTime(it.startTime, min)
        }
    }
}