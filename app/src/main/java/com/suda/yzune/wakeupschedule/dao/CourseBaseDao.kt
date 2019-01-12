package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseBean

@Dao
interface CourseBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourseBase(courseBaseBean: CourseBaseBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(courseBaseList: List<CourseBaseBean>)

    @Query("select * from coursebasebean where tableId = :tableId")
    fun getCourseBaseBeanOfTable(tableId: Int): LiveData<List<CourseBaseBean>>

    @Query("select * from coursebasebean where tableId = :tableId")
    fun getCourseBaseBeanOfTableInThread(tableId: Int): List<CourseBaseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where tableId = :tableId")
    fun getCourseOfTable(tableId: Int): LiveData<List<CourseBean>>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId")
    fun getCourseByDayOfTable(day: Int, tableId: Int): LiveData<List<CourseBean>>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId")
    fun getCourseByDayOfTableInThread(day: Int, tableId: Int): List<CourseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId and startWeek <= :week and endWeek >= :week and (type = 0 or type = :type)")
    fun getCourseByDayOfTableInThread(day: Int, week: Int, type: Int, tableId: Int): List<CourseBean>

    @Query("select * from coursebasebean where id = :id and tableId = :tableId")
    fun getCourseByIdOfTable(id: Int, tableId: Int): LiveData<CourseBaseBean>

    @Query("select * from coursebasebean where id = :id and tableId = :tableId")
    fun getCourseByIdOfTableInThread(id: Int, tableId: Int): CourseBaseBean

    @Query("select max(id) from coursebasebean where tableId = :tableId")
    fun getLastIdOfTable(tableId: Int): LiveData<Int>

    @Query("select max(id) from coursebasebean where tableId = :tableId")
    fun getLastIdOfTableInThread(tableId: Int): Int?

    @Query("delete from coursebasebean where id = :id and tableId = :tableId")
    fun deleteCourseBaseBeanOfTable(id: Int, tableId: Int)

    @Update
    fun updateCourseBaseBean(course: CourseBaseBean)

    @Query("select * from coursebasebean natural join coursedetailbean where courseName = :name and tableId = :tableId")
    fun checkSameNameInTable(name: String, tableId: Int): LiveData<CourseBaseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where courseName = :name and tableId = :tableId")
    fun checkSameNameInTableInThread(name: String, tableId: Int): CourseBaseBean?

    @Query("delete from coursebasebean where tableId = :tableId")
    fun removeCourseBaseBeanOfTable(tableId: Int)
}