package com.suda.yzune.wakeupschedule.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

@Dao
interface CourseDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(courseDetailList: List<CourseDetailBean>)

    @Query("select * from coursedetailbean where id = :id and tableName = :tableName")
    fun getDetailById(id: Int, tableName: String = ""): LiveData<List<CourseDetailBean>>

    @Query("delete from coursedetailbean where tableName = :tableName")
    fun deleteByTableName(tableName: String)

    @Query("delete from coursedetailbean where id = :id and tableName = :tableName")
    fun deleteByIdAndTableName(id: Int, tableName: String)

    @Query("select * from coursedetailbean where id = :id and tableName = :tableName")
    fun getDetailByIdAndTableNameInThread(id: Int, tableName: String): List<CourseDetailBean>

    @Update
    fun updateCourseDetail(courseDetailBean: CourseDetailBean)

    @Delete
    fun deleteCourseDetail(courseDetailBean: CourseDetailBean)

    @Query("select * from coursedetailbean where day = :day and startNode = :startNode and startWeek = :startWeek and type = :type and tableName = :tableName")
    fun getDetailByKeys(day: Int, startNode: Int, startWeek: Int, type: Int, tableName: String): List<CourseDetailBean>
}