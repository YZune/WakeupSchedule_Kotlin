package com.suda.yzune.wakeupschedule.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseBean

@Dao
interface CourseBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseBase(courseBaseBean: CourseBaseBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(courseBaseList: List<CourseBaseBean>)

    @Query("select * from coursebasebean natural join coursedetailbean where tableName = :tableName")
    fun getCourse(tableName: String = ""): LiveData<List<CourseBean>>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableName = :tableName")
    fun getCourseByDay(day: Int, tableName: String = ""): LiveData<List<CourseBean>>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableName = :tableName")
    fun getCourseByDayInThread(day: Int, tableName: String = ""): List<CourseBean>

    @Query("select * from coursebasebean where id = :id and tableName = :tableName")
    fun getCourseById(id: Int, tableName: String = ""): LiveData<CourseBaseBean>

    @Query("select * from coursebasebean where id = :id and tableName = :tableName")
    fun getCourseBeanByIdAndTableNameInThread(id: Int, tableName: String): CourseBaseBean

    @Query("select max(id) from coursebasebean where tableName = :tableName")
    fun getLastId(tableName: String = ""): LiveData<Int>

    @Query("delete from coursebasebean where id = :id and tableName = :tableName")
    fun deleteCourseBaseBean(id: Int, tableName: String)

    @Update
    fun updateCourseBaseBean(course: CourseBaseBean)

    @Query("select * from coursebasebean natural join coursedetailbean where courseName = :name and tableName = :tableName")
    fun checkSameName(name: String, tableName: String): LiveData<CourseBaseBean>

    @Query("delete from coursebasebean where tableName = :tableName")
    fun removeCourseData(tableName: String)
}