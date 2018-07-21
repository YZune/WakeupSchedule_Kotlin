package com.suda.yzune.wakeupschedule.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean

@Dao
interface CourseBaseDao {
    @Insert
    fun insertCourseBase(courseBaseBean: CourseBaseBean)

    @Insert
    fun insertList(courseBaseList: List<CourseBaseBean>)

    @Query("select * from coursebasebean")
    fun getAll(): LiveData<List<CourseBaseBean>>
}