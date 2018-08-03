package com.suda.yzune.wakeupschedule.course_add

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.schedule.ScheduleRepository
import java.util.*
import kotlin.collections.ArrayList

class AddCourseViewModel : ViewModel() {
    private var repository: AddCourseRepository? = null
    var newId = -1

    fun saveData() {
        repository!!.saveData(newId)
    }

    fun getUpdateFlag(): Boolean{
        return repository!!.getUpdateFlag()
    }

    fun removeInsert(){
        repository!!.removeInsert()
    }

    fun getSaveInfo(): LiveData<String> {
        return repository!!.getSaveInfo()
    }

    fun rollBackData(){
        return repository!!.rollBackData()
    }

    fun initRepository(context: Context) {
        if (repository == null) {
            repository = AddCourseRepository(context)
        }
    }

    fun initData(): MutableList<CourseEditBean> {
        return repository!!.initData()
    }

    fun initData(id: Int): LiveData<List<CourseDetailBean>> {
        return repository!!.initData(id)
    }

    fun initBaseData(id: Int): LiveData<CourseBaseBean> {
        return repository!!.initBaseData(id)
    }

    fun initBaseData(): CourseBaseBean {
        return repository!!.initBaseData()
    }


    fun getLastId(): LiveData<Int> {
        return repository!!.getLastId()
    }

    fun getList(): MutableList<CourseEditBean> {
        return repository!!.getList()
    }

    fun getBaseData(): CourseBaseBean {
        return repository!!.getBaseData()
    }

    fun checkSameName(): LiveData<CourseBaseBean> {
        return repository!!.checkSameName()
    }

    fun getDeleteList(): ArrayList<Int> {
        return repository!!.getDeleteList()
    }

    fun judgeType(list: ArrayList<Int>): Int {
        var flag = 0
        //0表示不是全30周的单周也不是全30周的双周
        if (list.size != 15) return flag
        if (list.contains(29)) {
            flag = 1
            for (i in 1..27 step 2) {
                if (!list.contains(i)) {
                    flag = 0
                    break
                }
            }
        }
        if (list.contains(30)) {
            flag = 2
            for (i in 2..28 step 2) {
                if (!list.contains(i)) {
                    flag = 0
                    break
                }
            }
        }
        return flag
    }
}