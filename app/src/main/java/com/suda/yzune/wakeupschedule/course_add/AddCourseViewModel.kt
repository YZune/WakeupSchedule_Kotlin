package com.suda.yzune.wakeupschedule.course_add

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean

class AddCourseViewModel : ViewModel() {
    private var repository: AddCourseRepository? = null
    var newId = -1

    fun saveData() {
        repository!!.preSaveData(newId)
    }

    fun getUpdateFlag(): Boolean {
        return repository!!.getUpdateFlag()
    }

    fun getSaveInfo(): LiveData<String> {
        return repository!!.getSaveInfo()
    }

    fun getWidgetIds(): ArrayList<Int> {
        return repository!!.getWidgetIds()
    }

    fun initRepository(context: Context) {
        if (repository == null) {
            repository = AddCourseRepository(context)
        }
    }

    fun initData(weeksNum: Long): MutableList<CourseEditBean> {
        return repository!!.initData(weeksNum)
    }

    fun initData(id: Int, tableName: String): LiveData<List<CourseDetailBean>> {
        return repository!!.initData(id, tableName)
    }

    fun initBaseData(id: Int, tableName: String): LiveData<CourseBaseBean> {
        return repository!!.initBaseData(id, tableName)
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

    fun judgeType(list: ArrayList<Int>, weeksNum: Int): Int {
        var flag = 0
        //0表示不是全部的单周也不是全部的双周
        if (weeksNum % 2 == 0 && list.size != weeksNum / 2) {
            return flag
        }
        if (weeksNum == list.size) {
            return flag
        }
        if (weeksNum % 2 != 0 && list.contains(weeksNum)) {
            flag = 1
            for (i in 1..weeksNum - 2 step 2) {
                if (!list.contains(i)) {
                    flag = 0
                    break
                }
            }
        }
        if (weeksNum % 2 != 0 && !list.contains(weeksNum)) {
            flag = 2
            for (i in 2..weeksNum - 2 step 2) {
                if (!list.contains(i)) {
                    flag = 0
                    break
                }
            }
        }
        if (weeksNum % 2 == 0 && list.contains(weeksNum)) {
            flag = 2
            for (i in 2..weeksNum - 2 step 2) {
                if (!list.contains(i)) {
                    flag = 0
                    break
                }
            }
        }
        if (weeksNum % 2 == 0 && !list.contains(weeksNum)) {
            flag = 1
            for (i in 1..weeksNum - 2 step 2) {
                if (!list.contains(i)) {
                    flag = 0
                    break
                }
            }
        }
        return flag
    }
}