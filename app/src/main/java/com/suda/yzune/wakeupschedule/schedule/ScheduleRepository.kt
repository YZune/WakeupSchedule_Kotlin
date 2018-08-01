package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.AsyncTask
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.dao.CourseBaseDao
import com.suda.yzune.wakeupschedule.dao.CourseDetailDao
import java.util.ArrayList

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
        result.value = list
        var i = 0
        while (i < raw.size) {
            if (i != raw.size - 1 && raw[i].id == raw[i + 1].id
                    && raw[i].room == raw[i + 1].room
                    && raw[i].startNode + raw[i].step == raw[i + 1].startNode) {
                raw[i].step += raw[i + 1].step
                list.add(raw[i])
                detailDao.updateCourseDetail(courseBean2DetailBean(raw[i]))
                detailDao.deleteCourseDetail(courseBean2DetailBean(raw[i + 1]))
                i += 2
            } else {
                list.add(raw[i])
                i++
            }
        }
        result.value = list

        return result
    }

    private fun courseBean2DetailBean(c: CourseBean): CourseDetailBean {
        return CourseDetailBean(
                id = c.id, room = c.room, day = c.day, teacher = c.teacher,
                startNode = c.startNode, step = c.step, startWeek = c.startWeek,
                endWeek = c.endWeek, tableName = c.tableName, type = c.type
        )
    }
}