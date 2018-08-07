package com.suda.yzune.wakeupschedule.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean

@Dao
interface AppWidgetDao {
    @Insert
    fun insertAppWidget(appWidgetBean: AppWidgetBean)

    @Query("delete from appwidgetbean where id = :id")
    fun deleteAppWidget(id: Int)

    @Query("select id from appwidgetbean where baseType = :baseType and detailType = :detailType")
    fun getIdsByTypes(baseType: Int, detailType: Int): List<Int>
}