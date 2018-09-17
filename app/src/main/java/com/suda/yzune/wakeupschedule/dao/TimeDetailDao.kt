package com.suda.yzune.wakeupschedule.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean

@Dao
interface TimeDetailDao {
    @Insert
    fun insertTimeList(list: List<TimeDetailBean>)

    @Query("insert into timetablebean values (null, :name)")
    fun insertTimeTable(name: String)

    @Query("insert into timedetailbean select node, startTime, endTime, :id from TimeDetailBean where timeTable = 0")
    fun initTimeDetail(id: Int)

    @Update
    fun updateTimeDetail(timeDetailBean: TimeDetailBean)

    @Update
    fun updateTimeDetailList(timeDetailBeanList: List<TimeDetailBean>)

    @Query("select * from timedetailbean where node < 17 order by node")
    fun getTimeList(): LiveData<List<TimeDetailBean>>

    @Query("select * from timedetailbean where node > 16 order by node")
    fun getSummerTimeList(): LiveData<List<TimeDetailBean>>

    @Query("select * from timedetailbean where node < 17 order by node")
    fun getTimeListInThread(): List<TimeDetailBean>

    @Query("select * from timedetailbean where node > 16 order by node")
    fun getSummerTimeListInThread(): List<TimeDetailBean>
}