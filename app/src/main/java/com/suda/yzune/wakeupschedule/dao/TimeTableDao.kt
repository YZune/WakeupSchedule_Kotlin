package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suda.yzune.wakeupschedule.bean.TimeTableBean

@Dao
interface TimeTableDao {
    @Insert
    fun insertTimeTable(timeTableBean: TimeTableBean)

    @Query("select * from timetablebean")
    fun getTimeTableList(): LiveData<List<TimeTableBean>>

    @Query("select max(id) from timetablebean")
    fun getMaxIdInThread(): Int

    @Query("select * from timetablebean where id = :id")
    fun getTimeTableInThread(id: Int): TimeTableBean?

    @Update
    fun updateTimeTable(timeTableBean: TimeTableBean)

    @Delete
    fun deleteTimeTable(timeTableBean: TimeTableBean)
}