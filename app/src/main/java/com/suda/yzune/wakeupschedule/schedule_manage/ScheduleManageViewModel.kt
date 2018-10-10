package com.suda.yzune.wakeupschedule.schedule_manage

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import kotlin.concurrent.thread

class ScheduleManageViewModel(application: Application) : AndroidViewModel(application) {

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()

    val tableSelectList = arrayListOf<TableSelectBean>()
    val editTableLiveData = MutableLiveData<TableBean>()

    fun initTableSelectList(): LiveData<List<TableSelectBean>> {
        return tableDao.getTableSelectList()
    }

    fun getTableById(id: Int) {
        thread(name = "getTableByIdThread") {
            editTableLiveData.postValue(tableDao.getTableByIdInThread(id))
        }
    }

    fun deleteTable(id: Int) {
        thread(name = "deleteTableThread") {
            try {
                tableDao.deleteTable(id)
            } catch (e: Exception) {

            }
        }
    }
}