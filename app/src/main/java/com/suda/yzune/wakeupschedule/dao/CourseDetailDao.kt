package com.suda.yzune.wakeupschedule.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

@Dao
interface CourseDetailDao {

    @Insert
    fun insertCourseDetail(courseDetailBean: CourseDetailBean)

    @Insert
    fun insertList(courseDetailList: List<CourseDetailBean>)

    @Query("select * from coursedetailbean")
    fun getAll(): LiveData<List<CourseDetailBean>>
}