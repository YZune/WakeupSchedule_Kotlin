package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.schedule_import.ImportRepository

class ScheduleViewModel : ViewModel() {

    lateinit var repository: ScheduleRepository

    fun initRepository(context: Context) {
        repository = ScheduleRepository(context)
    }

    fun getCourseByDay(raw: List<CourseBean>): LiveData<List<CourseBean>>{
        return repository.getCourseByDay(raw)
    }

    fun getRawCourseByDay(day: Int): LiveData<List<CourseBean>>{
        return repository.getRawCourseByDay(day)
    }
}