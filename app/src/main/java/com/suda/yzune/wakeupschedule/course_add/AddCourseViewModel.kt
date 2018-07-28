package com.suda.yzune.wakeupschedule.course_add

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.schedule.ScheduleRepository

class AddCourseViewModel : ViewModel() {
    private lateinit var repository: AddCourseRepository
    var newId = -1

    fun initRepository(context: Context) {
        repository = AddCourseRepository(context)
    }

    fun initData(type: Int): MutableList<CourseDetailBean> {
        return repository.initData(type)
    }

    fun getLastId(): LiveData<Int> {
        return repository.getLastId()
    }

    fun newBlankCourse(): CourseDetailBean {
        return repository.newBlankCourse()
    }

    fun getList(): MutableList<CourseDetailBean> {
        return repository.getList()
    }
}