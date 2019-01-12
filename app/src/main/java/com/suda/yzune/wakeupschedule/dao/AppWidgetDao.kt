package com.suda.yzune.wakeupschedule.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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