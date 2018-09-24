package com.suda.yzune.wakeupschedule.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import com.suda.yzune.wakeupschedule.bean.TimeTableBean

@Dao
interface TimeTableDao {
    @Insert
    fun insertTimeTable(timeTableBean: TimeTableBean)
}