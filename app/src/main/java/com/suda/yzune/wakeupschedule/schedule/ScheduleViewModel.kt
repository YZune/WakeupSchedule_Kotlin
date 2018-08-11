package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.schedule_import.ImportRepository
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ScheduleViewModel : ViewModel() {

    private lateinit var repository: ScheduleRepository

    fun initRepository(context: Context) {
        repository = ScheduleRepository(context)
    }

    fun getTimeDetailLiveList(): LiveData<List<TimeDetailBean>> {
        return repository.getTimeDetailLiveList()
    }

    fun updateFromOldVer(context: Context){
        repository.updateFromOldVer(context)
    }

    fun getTimeList(): ArrayList<TimeDetailBean> {
        return repository.getTimeDetailList()
    }

    fun getScheduleWidgetIds(): LiveData<List<Int>> {
        return repository.getScheduleWidgetIds()
    }

    fun getCourseByDay(raw: List<CourseBean>): LiveData<List<CourseBean>> {
        return repository.getCourseByDay(raw)
    }

    fun getRawCourseByDay(day: Int): LiveData<List<CourseBean>> {
        return repository.getRawCourseByDay(day)
    }

    fun deleteCourseBean(courseBean: CourseBean) {
        repository.deleteCourseBean(courseBean)
    }

    fun deleteCourseBaseBean(courseBean: CourseBean) {
        repository.deleteCourseBaseBean(courseBean.id, courseBean.tableName)
    }

    fun updateCourseBaseBean(courseBean: CourseBean) {
        repository.updateCourseBaseBean(CourseUtils.courseBean2BaseBean(courseBean))
    }

}