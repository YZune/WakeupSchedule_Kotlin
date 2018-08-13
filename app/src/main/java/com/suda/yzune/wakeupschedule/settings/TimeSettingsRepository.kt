package com.suda.yzune.wakeupschedule.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import java.sql.Time
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

    fun getSaveInfo(): LiveData<String> {
        return saveInfo
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