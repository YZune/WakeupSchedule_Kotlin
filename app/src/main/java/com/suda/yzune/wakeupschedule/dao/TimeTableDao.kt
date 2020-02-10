package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.bean.TimeTableBean

@Dao
interface TimeTableDao {

    @Transaction
    suspend fun initTimeTable(timeTableBean: TimeTableBean) {
        val id = insertTimeTable(timeTableBean).toInt()
        val timeList = listOf(
                TimeDetailBean(1, "08:00", "08:50", id),
                TimeDetailBean(2, "09:00", "09:50", id),
                TimeDetailBean(3, "10:10", "11:00", id),
                TimeDetailBean(4, "11:10", "12:00", id),
                TimeDetailBean(5, "13:30", "14:20", id),
                TimeDetailBean(6, "14:30", "15:20", id),
                TimeDetailBean(7, "15:40", "16:30", id),
                TimeDetailBean(8, "16:40", "17:30", id),
                TimeDetailBean(9, "18:30", "19:20", id),
                TimeDetailBean(10, "19:30", "20:20", id),
                TimeDetailBean(11, "20:30", "21:20", id),
                TimeDetailBean(12, "00:00", "00:00", id),
                TimeDetailBean(13, "00:00", "00:00", id),
                TimeDetailBean(14, "00:00", "00:00", id),
                TimeDetailBean(15, "00:00", "00:00", id),
                TimeDetailBean(16, "00:00", "00:00", id),
                TimeDetailBean(17, "00:00", "00:00", id),
                TimeDetailBean(18, "00:00", "00:00", id),
                TimeDetailBean(19, "00:00", "00:00", id),
                TimeDetailBean(20, "00:00", "00:00", id),
                TimeDetailBean(21, "00:00", "00:00", id),
                TimeDetailBean(22, "00:00", "00:00", id),
                TimeDetailBean(23, "00:00", "00:00", id),
                TimeDetailBean(24, "00:00", "00:00", id),
                TimeDetailBean(25, "00:00", "00:00", id),
                TimeDetailBean(26, "00:00", "00:00", id),
                TimeDetailBean(27, "00:00", "00:00", id),
                TimeDetailBean(28, "00:00", "00:00", id),
                TimeDetailBean(29, "00:00", "00:00", id),
                TimeDetailBean(30, "00:00", "00:00", id)
        )
        insertTimeList(timeList)
    }

    @Insert
    suspend fun insertTimeList(list: List<TimeDetailBean>)

    @Insert
    suspend fun insertTimeTable(timeTableBean: TimeTableBean): Long

    @Query("select * from timetablebean")
    fun getTimeTableList(): LiveData<List<TimeTableBean>>

    @Query("select max(id) from timetablebean")
    suspend fun getMaxId(): Int

    @Query("select * from timetablebean where id = :id")
    suspend fun getTimeTable(id: Int): TimeTableBean?

    @Update
    suspend fun updateTimeTable(timeTableBean: TimeTableBean)

    @Delete
    suspend fun deleteTimeTable(timeTableBean: TimeTableBean)
}