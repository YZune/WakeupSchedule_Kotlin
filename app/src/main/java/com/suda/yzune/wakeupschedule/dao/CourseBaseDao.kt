package com.suda.yzune.wakeupschedule.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseBean

@Dao
interface CourseBaseDao {
    @Insert
    fun insertCourseBase(courseBaseBean: CourseBaseBean)

    @Insert
    fun insertList(courseBaseList: List<CourseBaseBean>)

    @Query("select * from coursebasebean natural join coursedetailbean")
    fun getCourse(): LiveData<List<CourseBean>>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day")
    fun getCourseByDay(day: Int): LiveData<List<CourseBean>>

    @Query("select max(id) from coursebasebean")
    fun getLastId(): LiveData<Int>
}