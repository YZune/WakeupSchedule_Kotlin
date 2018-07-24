package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBean
import java.util.ArrayList

class ScheduleRepository(context: Context) {

    var courseData: Array<List<CourseBean>> = arrayOf<List<CourseBean>>()
    val empty = emptyList<Int>()

    private val dataBase = AppDatabase.getDatabase(context)
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()

    fun getRawCourseByDay(day: Int): LiveData<List<CourseBean>> {
        return baseDao.getCourseByDay(day)
    }

    fun getCourseByDay(raw: List<CourseBean>): LiveData<List<CourseBean>> {
        val result = MutableLiveData<List<CourseBean>>()
        val list = ArrayList<CourseBean>()
        result.value = list
        var i = 0
        while (i < raw.size) {
            if (i != raw.size - 1 && raw[i].id == raw[i + 1].id
                    && raw[i].room == raw[i + 1].room
                    && raw[i].startNode + raw[i].step == raw[i + 1].startNode) {
                raw[i].step += raw[i + 1].step
                list.add(raw[i])
                i += 2
            } else {
                list.add(raw[i])
                i++
            }
        }
        result.value = list
        return result
    }
}