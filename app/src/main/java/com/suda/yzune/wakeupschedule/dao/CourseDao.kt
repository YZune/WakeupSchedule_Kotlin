package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

@Dao
interface CourseDao {

    @Transaction
    suspend fun insertSingleCourse(courseBaseBean: CourseBaseBean, courseDetailList: List<CourseDetailBean>) {
        insertCourseBase(courseBaseBean)
        insertDetailList(courseDetailList)
    }

    @Transaction
    suspend fun updateSingleCourse(courseBaseBean: CourseBaseBean, courseDetailList: List<CourseDetailBean>) {
        updateCourseBaseBean(courseBaseBean)
        deleteDetailByIdOfTable(courseBaseBean.id, courseBaseBean.tableId)
        insertDetailList(courseDetailList)
    }

    @Transaction
    suspend fun insertCourses(courseBaseList: List<CourseBaseBean>, courseDetailList: List<CourseDetailBean>) {
        insertBaseList(courseBaseList)
        insertDetailList(courseDetailList)
    }

    @Delete
    suspend fun deleteCourseDetail(courseDetailBean: CourseDetailBean)

    @Query("select * from coursebasebean where tableId = :tableId")
    fun getCourseBaseBeanOfTableLiveData(tableId: Int): LiveData<List<CourseBaseBean>>

    @Query("select * from coursebasebean where tableId = :tableId")
    suspend fun getCourseBaseBeanOfTable(tableId: Int): List<CourseBaseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId")
    fun getCourseByDayOfTable(day: Int, tableId: Int): LiveData<List<CourseBean>>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId")
    fun getCourseByDayOfTableInThread(day: Int, tableId: Int): List<CourseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId and startWeek <= :week and endWeek >= :week and (type = 0 or type = :type)")
    fun getCourseByDayOfTableInThread(day: Int, week: Int, type: Int, tableId: Int): List<CourseBean>

    @Query("select * from coursebasebean where id = :id and tableId = :tableId")
    fun getCourseByIdOfTableInThread(id: Int, tableId: Int): CourseBaseBean

    @Query("select max(id) from coursebasebean where tableId = :tableId")
    fun getLastIdOfTableInThread(tableId: Int): Int?

    @Query("delete from coursebasebean where id = :id and tableId = :tableId")
    fun deleteCourseBaseBeanOfTable(id: Int, tableId: Int)

    @Query("select * from coursebasebean natural join coursedetailbean where courseName = :name and tableId = :tableId")
    fun checkSameNameInTableInThread(name: String, tableId: Int): CourseBaseBean?

    @Query("delete from coursebasebean where tableId = :tableId")
    fun removeCourseBaseBeanOfTable(tableId: Int)

    @Query("delete from coursedetailbean where id = :id and tableId = :tableId")
    fun deleteDetailByIdOfTable(id: Int, tableId: Int)

    @Query("select * from coursedetailbean where id = :id and tableId = :tableId")
    fun getDetailByIdOfTableInThread(id: Int, tableId: Int): List<CourseDetailBean>

    @Query("select * from coursedetailbean where tableId = :tableId")
    fun getDetailOfTableInThread(tableId: Int): List<CourseDetailBean>

    @Query("select distinct teacher from coursedetailbean where tableId = :tableId order by length(teacher)")
    suspend fun getExistedTeachers(tableId: Int): List<String>

    @Query("select distinct room from coursedetailbean where tableId = :tableId order by length(room)")
    suspend fun getExistedRooms(tableId: Int): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaseList(courseBaseList: List<CourseBaseBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseBase(courseBaseBean: CourseBaseBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailList(courseDetailList: List<CourseDetailBean>)

    @Update
    fun updateCourseBaseBean(course: CourseBaseBean)

}