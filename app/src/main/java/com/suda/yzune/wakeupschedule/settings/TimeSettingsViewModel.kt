package com.suda.yzune.wakeupschedule.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.schedule.ScheduleRepository

class TimeSettingsViewModel : ViewModel() {
    private lateinit var repository: TimeSettingsRepository

    fun initRepository(context: Context) {
        repository = TimeSettingsRepository(context)
        repository.initTimeSelectList()
    }

    fun getTimeList(): ArrayList<TimeDetailBean>{
        return repository.getTimeList()
    }

    fun getSaveInfo(): LiveData<String>{
        return repository.getSaveInfo()
    }

    fun saveData(){
        repository.saveData()
    }

    fun getDetailData(): LiveData<List<TimeDetailBean>> {
        return repository.getDetailData()
    }

    fun getTimeSelectList(): ArrayList<String>{
        return repository.getTimeSelectList()
    }
}