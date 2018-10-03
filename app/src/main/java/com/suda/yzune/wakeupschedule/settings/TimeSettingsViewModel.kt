package com.suda.yzune.wakeupschedule.settings

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.bean.TimeTableBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlin.concurrent.thread

class TimeSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val timeDao = dataBase.timeDetailDao()
    private val timeTableDao = dataBase.timeTableDao()
    val timeTableList = arrayListOf<TimeTableBean>()
    val timeList = arrayListOf<TimeDetailBean>()
    val timeSelectList = arrayListOf<String>()
    val saveInfo = MutableLiveData<String>()

    fun addNewTimeTable(name: String) {
        thread(name = "addNewTimeTableThread") {
            try {
                timeTableDao.insertTimeTable(TimeTableBean(id = 0, name = name))
            } catch (e: Exception) {

            }
        }
    }

    fun initTimeTableData(id: Int) {
        thread(name = "InitTimeTableDataThread") {
            try {
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
                }
                timeDao.insertTimeList(timeList)
            } catch (e: Exception) {

            }
        }
    }

    fun getTimeTableList(): LiveData<List<TimeTableBean>> {
        return timeTableDao.getTimeTableList()
    }

    fun getTimeData(id: Int): LiveData<List<TimeDetailBean>> {
        return timeDao.getTimeList(id)
    }

    fun saveData() {
        thread(name = "updateTimeDetailThread") {
            timeDao.updateTimeDetailList(timeList)
            saveInfo.postValue("ok")
        }
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