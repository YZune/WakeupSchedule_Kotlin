package com.suda.yzune.wakeupschedule.course_add

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.schedule.ScheduleRepository

class AddCourseViewModel : ViewModel() {
    private var repository: AddCourseRepository? = null
    var newId = -1

    fun initRepository(context: Context) {
        if (repository == null) {
            repository = AddCourseRepository(context)
        }
    }

    fun initData(type: Int): MutableList<CourseDetailBean> {
        return repository!!.initData(type)
    }

    fun getLastId(): LiveData<Int> {
        return repository!!.getLastId()
    }

    fun newBlankCourse(): CourseDetailBean {
        return repository!!.newBlankCourse()
    }

    fun getList(): MutableList<CourseDetailBean> {
        return repository!!.getList()
    }

    fun getWeekMap(): MutableMap<Int, ArrayList<Int>> {
        return repository!!.getWeekMap()
    }

    fun initWeekArrayList(position: Int) {
        val result = arrayListOf<Int>()
        when (getList()[position].type) {
            0 -> {
                for (i in getList()[position].startWeek..getList()[position].endWeek) {
                    result.add(i)
                }
            }
            else -> {
                for (i in getList()[position].startWeek..getList()[position].endWeek step 2) {
                    result.add(i)
                }
            }
        }
        getWeekMap()[position] = result
    }

    fun judgeType(list: ArrayList<Int>){
        var flag = 0
        //0表示不是全30周的单周也不是全30周的双周
        if (list.size != 15) return
    }
}