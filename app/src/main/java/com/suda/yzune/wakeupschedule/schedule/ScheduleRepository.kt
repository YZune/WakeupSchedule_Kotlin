package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.dao.CourseBaseDao
import com.suda.yzune.wakeupschedule.dao.CourseDetailDao
import com.suda.yzune.wakeupschedule.utils.CourseUtils.courseBean2DetailBean
import java.util.ArrayList
import kotlin.concurrent.thread

class ScheduleRepository(context: Context) {

    private val dataBase = AppDatabase.getDatabase(context)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()

    fun getRawCourseByDay(day: Int): LiveData<List<CourseBean>> {
        return baseDao.getCourseByDay(day)
    }

    fun getCourseByDay(raw: List<CourseBean>): LiveData<List<CourseBean>> {
        val result = MutableLiveData<List<CourseBean>>()
        val list = ArrayList<CourseBean>()
        val changeIndex = arrayListOf<Int>()
        result.value = list
        var i = 0
        while (i < raw.size) {
            if (i != raw.size - 1 && raw[i].id == raw[i + 1].id
                    && raw[i].room == raw[i + 1].room
                    && raw[i].startNode + raw[i].step == raw[i + 1].startNode) {
                raw[i].step += raw[i + 1].step
                list.add(raw[i])
                changeIndex.add(i)
                i += 2
            } else {
                list.add(raw[i])
                i++
            }
        }
        result.value = list
        thread(name = "MakeTogetherThread") {
            for (index in changeIndex) {
                detailDao.updateCourseDetail(courseBean2DetailBean(raw[index]))
                detailDao.deleteCourseDetail(courseBean2DetailBean(raw[index + 1]))
            }
        }
        return result
    }

    fun deleteCourseBean(courseBean: CourseBean) {
        thread(name = "DeleteCourseBeanThread") {
            detailDao.deleteCourseDetail(courseBean2DetailBean(courseBean))
        }
    }

    fun deleteCourseBaseBean(id: Int, tableName: String){
        thread(name = "DeleteCourseBaseBeanThread") {
            baseDao.deleteCourseBaseBean(id, tableName)
        }
    }

    fun updateCourseBaseBean(course: CourseBaseBean){
        thread(name = "UpdateCourseBaseBeanThread") {
            baseDao.updateCourseBaseBean(course)
        }
    }
}