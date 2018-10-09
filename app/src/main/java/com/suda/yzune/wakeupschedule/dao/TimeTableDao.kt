package com.suda.yzune.wakeupschedule.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.suda.yzune.wakeupschedule.bean.TimeTableBean

@Dao
interface TimeTableDao {
    @Insert
    fun insertTimeTable(timeTableBean: TimeTableBean)

    @Query("select * from timetablebean")
    fun getTimeTableList(): LiveData<List<TimeTableBean>>

    @Update
    fun updateTimeTable(timeTableBean: TimeTableBean)

    @Delete
    fun deleteTimeTable(timeTableBean: TimeTableBean)
}