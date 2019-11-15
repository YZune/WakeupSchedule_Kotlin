package com.suda.yzune.wakeupschedule.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

@Dao
interface CourseDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(courseDetailList: List<CourseDetailBean>)

    @Query("select * from coursedetailbean where id = :id and tableId = :tableId")
    fun getDetailByIdOfTable(id: Int, tableId: Int): LiveData<List<CourseDetailBean>>

    @Query("delete from coursedetailbean where id = :id and tableId = :tableId")
    fun deleteByIdOfTable(id: Int, tableId: Int)

    @Query("select * from coursedetailbean where id = :id and tableId = :tableId")
    fun getDetailByIdOfTableInThread(id: Int, tableId: Int): List<CourseDetailBean>

    @Query("select * from coursedetailbean where tableId = :tableId")
    fun getDetailOfTableInThread(tableId: Int): List<CourseDetailBean>

    @Update
    fun updateCourseDetail(courseDetailBean: CourseDetailBean)

    @Delete
    fun deleteCourseDetail(courseDetailBean: CourseDetailBean)

    @Query("select * from coursedetailbean where id = :id and day = :day and startNode = :startNode and startWeek = :startWeek and type = :type and tableId = :tableId")
    fun getDetailByKeys(id: Int, day: Int, startNode: Int, startWeek: Int, type: Int, tableId: Int): List<CourseDetailBean>

    @Query("select distinct teacher from coursedetailbean where tableId = :tableId order by length(teacher)")
    suspend fun getExistedTeachers(tableId: Int) : List<String>
}