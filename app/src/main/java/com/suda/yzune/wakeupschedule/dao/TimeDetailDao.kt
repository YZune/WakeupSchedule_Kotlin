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
    suspend fun updateTimeDetailList(timeDetailBeanList: List<TimeDetailBean>)

    @Query("select * from timedetailbean where timeTable = :id order by node")
    fun getTimeListLiveData(id: Int): LiveData<List<TimeDetailBean>>

    @Query("select * from timedetailbean where timeTable = :id order by node")
    suspend fun getTimeList(id: Int): List<TimeDetailBean>

    @Query("select * from timedetailbean where timeTable = :id order by node")
    fun getTimeListSync(id: Int): List<TimeDetailBean>
}