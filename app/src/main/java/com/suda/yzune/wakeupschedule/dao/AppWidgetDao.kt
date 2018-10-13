package com.suda.yzune.wakeupschedule.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean

@Dao
interface AppWidgetDao {
    @Insert
    fun insertAppWidget(appWidgetBean: AppWidgetBean)

    @Query("update appwidgetbean set info = '1'")
    fun updateFromOldVer()

    @Query("delete from appwidgetbean where id = :id")
    fun deleteAppWidget(id: Int)

    @Query("select * from appwidgetbean where baseType = :baseType and detailType = :detailType")
    fun getWidgetsByTypesInThread(baseType: Int, detailType: Int): List<AppWidgetBean>

    @Query("select * from appwidgetbean where baseType = :baseType and detailType = :detailType")
    fun getWidgetsByTypes(baseType: Int, detailType: Int): LiveData<List<AppWidgetBean>>

    @Query("select id from appwidgetbean where baseType = :baseType and detailType = :detailType")
    fun getWidgetIdsByTypes(baseType: Int, detailType: Int): LiveData<List<Int>>

    @Query("select id from appwidgetbean where baseType = 0 and detailType = 0 and info = :tableId")
    fun getIdsOfWeekTypeOfTable(tableId: String): LiveData<List<Int>>

    @Query("select id from appwidgetbean where baseType = 0 and detailType = 0 and info = :tableId")
    fun getIdsOfWeekTypeOfTableInThread(tableId: String): List<Int>
}