package com.suda.yzune.wakeupschedule.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean

@Dao
interface AppWidgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppWidget(appWidgetBean: AppWidgetBean)

    @Query("update appwidgetbean set info = '1'")
    suspend fun updateFromOldVer()

    @Query("delete from appwidgetbean where id = :id")
    suspend fun deleteAppWidget(id: Int)

    @Query("select * from appwidgetbean where baseType = :baseType and detailType = :detailType")
    suspend fun getWidgetsByTypes(baseType: Int, detailType: Int): List<AppWidgetBean>

    @Query("select * from appwidgetbean where baseType = :baseType")
    suspend fun getWidgetsByBaseType(baseType: Int): List<AppWidgetBean>
}