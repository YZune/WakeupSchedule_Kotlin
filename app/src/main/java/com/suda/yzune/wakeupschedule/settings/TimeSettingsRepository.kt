package com.suda.yzune.wakeupschedule.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import kotlin.concurrent.thread

class TimeSettingsRepository(context: Context) {
    private val dataBase = AppDatabase.getDatabase(context)
    private val timeDao = dataBase.timeDetailDao()
    private val timeList = arrayListOf<TimeDetailBean>()
    private val timeSelectList = arrayListOf<String>()
    private val saveInfo = MutableLiveData<String>()

    fun saveData() {
        for (i in 0 until timeList.size - 1) {
            if (timeList[i].startTime > timeList[i + 1].endTime) {
                saveInfo.value = "时间的顺序不太对哦"
                return
            }
        }

        thread(name = "updateTimeDetailThread") {
            timeDao.updateTimeDetailList(timeList)
            saveInfo.postValue("ok")
        }
    }

    fun getSaveInfo(): LiveData<String>{
        return saveInfo
    }

    fun initSudaTime(context: Context) {
        if (!PreferenceUtils.getBooleanFromSP(context.applicationContext, "isInitTimeTable", false)) {
            timeList.add(TimeDetailBean(1, "08:00", "08:50"))
            timeList.add(TimeDetailBean(2, "09:00", "09:50"))
            timeList.add(TimeDetailBean(3, "10:10", "11:00"))
            timeList.add(TimeDetailBean(4, "11:10", "12:00"))
            timeList.add(TimeDetailBean(5, "13:30", "14:20"))
            timeList.add(TimeDetailBean(6, "14:30", "15:20"))
            timeList.add(TimeDetailBean(7, "15:40", "16:30"))
            timeList.add(TimeDetailBean(8, "16:40", "17:30"))
            timeList.add(TimeDetailBean(9, "18:30", "19:20"))
            timeList.add(TimeDetailBean(10, "19:30", "20:20"))
            timeList.add(TimeDetailBean(11, "20:30", "21:20"))
            timeList.add(TimeDetailBean(12, "00:00", "00:00"))
            timeList.add(TimeDetailBean(13, "00:00", "00:00"))
            timeList.add(TimeDetailBean(14, "00:00", "00:00"))
            timeList.add(TimeDetailBean(15, "00:00", "00:00"))
            timeList.add(TimeDetailBean(16, "00:00", "00:00"))
            thread(name = "initTimeTableThread") {
                try {
                    timeDao.insertTimeList(timeList)
                } catch (e: SQLiteConstraintException) {

                }
                PreferenceUtils.saveBooleanToSP(context.applicationContext, "isInitTimeTable", true)
            }
        }
    }

    fun getTimeList(): ArrayList<TimeDetailBean> {
        return timeList
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

    fun getTimeSelectList(): ArrayList<String> {
        return timeSelectList
    }

    fun getDetailData(): LiveData<List<TimeDetailBean>> {
        return timeDao.getTimeList()
    }
}