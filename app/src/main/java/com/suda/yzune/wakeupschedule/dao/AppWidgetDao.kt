package com.suda.yzune.wakeupschedule.dao

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

    @Query("select * from appwidgetbean where baseType = :baseType")
    fun getWidgetsByBaseTypeInThread(baseType: Int): List<AppWidgetBean>
}