package com.suda.yzune.wakeupschedule.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlin.concurrent.thread

class TimeSettingsRepository(context: Context) {
    private val dataBase = AppDatabase.getDatabase(context)
    private val timeDao = dataBase.timeDetailDao()
    private val timeList = arrayListOf<TimeDetailBean>()
    private val summerTimeList = arrayListOf<TimeDetailBean>()
    private val timeSelectList = arrayListOf<String>()
    private val saveInfo = MutableLiveData<String>()
    private val refreshMsg = MutableLiveData<Int>()

    fun saveData() {
        for (i in 0 until timeList.size - 1) {
            if (timeList[i].endTime > timeList[i + 1].startTime) {
                saveInfo.value = "时间的顺序不太对哦"
                return
            }
        }

        for (i in 0 until summerTimeList.size - 1) {
            if (summerTimeList[i].endTime > summerTimeList[i + 1].startTime) {
                saveInfo.value = "时间的顺序不太对哦"
                return
            }
        }

        thread(name = "updateTimeDetailThread") {
            timeDao.updateTimeDetailList(timeList)
            timeDao.updateTimeDetailList(summerTimeList)
            saveInfo.postValue("ok")
        }
    }

    fun refreshEndTime(min: Int) {
        timeList.forEach {
            it.endTime = CourseUtils.calAfterTime(it.startTime, min)
        }
        summerTimeList.forEach {
            it.endTime = CourseUtils.calAfterTime(it.startTime, min)
        }
        refreshMsg.value = min
    }

    fun getRefreshMsg(): LiveData<Int> {
        return refreshMsg
    }

    fun getSaveInfo(): LiveData<String> {
        return saveInfo
    }

    fun getTimeList(): ArrayList<TimeDetailBean> {
        return timeList
    }

    fun getSummerTimeList(): ArrayList<TimeDetailBean> {
        return summerTimeList
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

    fun getSummerData(): LiveData<List<TimeDetailBean>> {
        return timeDao.getSummerTimeList()
    }
}