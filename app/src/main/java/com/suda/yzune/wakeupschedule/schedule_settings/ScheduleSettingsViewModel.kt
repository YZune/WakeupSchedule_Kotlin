package com.suda.yzune.wakeupschedule.schedule_settings

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TableBean

class ScheduleSettingsViewModel(application: Application) : AndroidViewModel(application) {

    var mYear = 2018
    var mMonth = 9
    var mDay = 20
    lateinit var table: TableBean
    lateinit var termStartList: List<String>

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()

    fun initTableData(json: String) {
        val gson = Gson()
        table = gson.fromJson<TableBean>(json, object : TypeToken<TableBean>() {}.type)
    }

    fun saveSettings() {
        tableDao.updateTable(table)
    }
}