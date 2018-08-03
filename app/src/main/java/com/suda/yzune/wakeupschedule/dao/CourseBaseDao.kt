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

    @Query("select * from coursebasebean where id = :id")
    fun getCourseById(id: Int): LiveData<CourseBaseBean>

    @Query("select * from coursebasebean where id = :id and tableName = :tableName")
    fun getCourseBeanByIdAndTableNameInThread(id: Int, tableName: String): CourseBaseBean

    @Query("select max(id) from coursebasebean")
    fun getLastId(): LiveData<Int>

    @Query("delete from coursebasebean where id = :id and tableName = :tableName")
    fun deleteCourseBaseBean(id: Int, tableName: String)

    @Update
    fun updateCourseBaseBean(course: CourseBaseBean)

    @Query("select * from coursebasebean natural join coursedetailbean where courseName = :name and tableName = :tableName")
    fun checkSameName(name: String, tableName: String): LiveData<CourseBaseBean>
}