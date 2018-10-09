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