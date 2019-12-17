package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean

@Dao
interface TimeDetailDao {
    @Insert
    suspend fun insertTimeList(list: List<TimeDetailBean>)

    @Update
    fun updateTimeDetail(timeDetailBean: TimeDetailBean)

    @Update
    fun updateTimeDetailList(timeDetailBeanList: List<TimeDetailBean>)

    @Query("select * from timedetailbean where timeTable = :id order by node")
    fun getTimeList(id: Int): LiveData<List<TimeDetailBean>>

    @Query("select count(*) from timedetailbean where timeTable = :id")
    fun getTimeListSize(id: Int): LiveData<Int>

    @Query("select * from timedetailbean where timeTable = :id order by node")
    fun getTimeListInThread(id: Int): List<TimeDetailBean>
}