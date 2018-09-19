package com.suda.yzune.wakeupschedule.settings

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlin.concurrent.thread

class TimeSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val timeDao = dataBase.timeDetailDao()
    val timeList = arrayListOf<TimeDetailBean>()
    val timeSelectList = arrayListOf<String>()
    val saveInfo = MutableLiveData<String>()
    val refreshMsg = MutableLiveData<Int>()

    fun getTimeData(maxNode: Int, id: Int): LiveData<List<TimeDetailBean>> {
        return timeDao.getTimeList(maxNode)
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
        refreshMsg.value = min
    }
}