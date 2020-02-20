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
    suspend fun updateSameCourse(courseBaseBean: CourseBaseBean, courseDetailList: List<CourseDetailBean>) {
        updateCourseBaseBean(courseBaseBean)
        insertDetailList(courseDetailList)
    }

    @Transaction
    suspend fun insertCourses(courseBaseList: List<CourseBaseBean>, courseDetailList: List<CourseDetailBean>) {
        insertBaseList(courseBaseList)
        insertDetailList(courseDetailList)
    }

    @Transaction
    suspend fun coverImport(courseBaseList: List<CourseBaseBean>, courseDetailList: List<CourseDetailBean>) {
        removeCourseBaseBeanOfTable(courseBaseList[0].tableId)
        insertBaseList(courseBaseList)
        insertDetailList(courseDetailList)
    }

    @Delete
    suspend fun deleteCourseDetail(courseDetailBean: CourseDetailBean)

    @Query("select * from coursebasebean where tableId = :tableId")
    suspend fun getCourseBaseBeanOfTable(tableId: Int): List<CourseBaseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId")
    fun getCourseByDayOfTableLiveData(day: Int, tableId: Int): LiveData<List<CourseBean>>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId")
    suspend fun getCourseByDayOfTable(day: Int, tableId: Int): List<CourseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId")
    fun getCourseByDayOfTableSync(day: Int, tableId: Int): List<CourseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId and startWeek <= :week and endWeek >= :week and (type = 0 or type = :type)")
    suspend fun getCourseByDayOfTable(day: Int, week: Int, type: Int, tableId: Int): List<CourseBean>

    @Query("select * from coursebasebean natural join coursedetailbean where day = :day and tableId = :tableId and startWeek <= :week and endWeek >= :week and (type = 0 or type = :type)")
    fun getCourseByDayOfTableSync(day: Int, week: Int, type: Int, tableId: Int): List<CourseBean>

    @Query("select * from coursebasebean where id = :id and tableId = :tableId")
    suspend fun getCourseByIdOfTable(id: Int, tableId: Int): CourseBaseBean

    @Query("select max(id) from coursebasebean where tableId = :tableId")
    suspend fun getLastIdOfTable(tableId: Int): Int?

    @Query("delete from coursebasebean where id = :id and tableId = :tableId")
    suspend fun deleteCourseBaseBeanOfTable(id: Int, tableId: Int)

    @Query("select * from coursebasebean natural join coursedetailbean where courseName = :name and tableId = :tableId")
    suspend fun checkSameNameInTable(name: String, tableId: Int): CourseBaseBean?

    @Query("delete from coursebasebean where tableId = :tableId")
    suspend fun removeCourseBaseBeanOfTable(tableId: Int)

    @Query("delete from coursedetailbean where id = :id and tableId = :tableId")
    suspend fun deleteDetailByIdOfTable(id: Int, tableId: Int)

    @Query("select * from coursedetailbean where id = :id and tableId = :tableId")
    suspend fun getDetailByIdOfTable(id: Int, tableId: Int): List<CourseDetailBean>

    @Query("select * from coursedetailbean where tableId = :tableId")
    suspend fun getDetailOfTable(tableId: Int): List<CourseDetailBean>

    @Query("select distinct teacher from coursedetailbean where tableId = :tableId order by length(teacher)")
    suspend fun getExistedTeachers(tableId: Int): List<String>

    @Query("select distinct room from coursedetailbean where tableId = :tableId order by length(room)")
    suspend fun getExistedRooms(tableId: Int): List<String>

    @Query("SELECT COUNT(*) FROM coursedetailbean WHERE tableId=:tableId AND ((startWeek<=:week AND endWeek>=:week) AND (type=0 OR (:week % 2=0 AND type=2) OR (:week % 2=1 AND type=1)))")
    fun getShowCourseNumber(tableId: Int, week: Int): LiveData<Int>

    @Query("SELECT COUNT(*) FROM coursedetailbean WHERE tableId=:tableId AND endWeek>=:week")
    fun getShowCourseNumberWithOtherWeek(tableId: Int, week: Int): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaseList(courseBaseList: List<CourseBaseBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseBase(courseBaseBean: CourseBaseBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailList(courseDetailList: List<CourseDetailBean>)

    @Update
    suspend fun updateCourseBaseBean(course: CourseBaseBean)

}