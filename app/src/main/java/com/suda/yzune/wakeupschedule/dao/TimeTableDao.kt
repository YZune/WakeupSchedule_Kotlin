package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.bean.TimeTableBean

@Dao
interface TimeTableDao {

    @Transaction
    fun initTimeTable(timeTableBean: TimeTableBean) {
        val id = insertTimeTable(timeTableBean).toInt()
        val timeList = ArrayList<TimeDetailBean>().apply {
            add(TimeDetailBean(1, "08:00", "08:50", id))
            add(TimeDetailBean(2, "09:00", "09:50", id))
            add(TimeDetailBean(3, "10:10", "11:00", id))
            add(TimeDetailBean(4, "11:10", "12:00", id))
            add(TimeDetailBean(5, "13:30", "14:20", id))
            add(TimeDetailBean(6, "14:30", "15:20", id))
            add(TimeDetailBean(7, "15:40", "16:30", id))
            add(TimeDetailBean(8, "16:40", "17:30", id))
            add(TimeDetailBean(9, "18:30", "19:20", id))
            add(TimeDetailBean(10, "19:30", "20:20", id))
            add(TimeDetailBean(11, "20:30", "21:20", id))
            add(TimeDetailBean(12, "00:00", "00:00", id))
            add(TimeDetailBean(13, "00:00", "00:00", id))
            add(TimeDetailBean(14, "00:00", "00:00", id))
            add(TimeDetailBean(15, "00:00", "00:00", id))
            add(TimeDetailBean(16, "00:00", "00:00", id))
            add(TimeDetailBean(17, "00:00", "00:00", id))
            add(TimeDetailBean(18, "00:00", "00:00", id))
            add(TimeDetailBean(19, "00:00", "00:00", id))
            add(TimeDetailBean(20, "00:00", "00:00", id))
            add(TimeDetailBean(21, "00:00", "00:00", id))
            add(TimeDetailBean(22, "00:00", "00:00", id))
            add(TimeDetailBean(23, "00:00", "00:00", id))
            add(TimeDetailBean(24, "00:00", "00:00", id))
            add(TimeDetailBean(25, "00:00", "00:00", id))
            add(TimeDetailBean(26, "00:00", "00:00", id))
            add(TimeDetailBean(27, "00:00", "00:00", id))
            add(TimeDetailBean(28, "00:00", "00:00", id))
            add(TimeDetailBean(29, "00:00", "00:00", id))
            add(TimeDetailBean(30, "00:00", "00:00", id))
        }
        insertTimeList(timeList)
    }

    @Insert
    fun insertTimeList(list: List<TimeDetailBean>)

    @Insert
    fun insertTimeTable(timeTableBean: TimeTableBean): Long

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