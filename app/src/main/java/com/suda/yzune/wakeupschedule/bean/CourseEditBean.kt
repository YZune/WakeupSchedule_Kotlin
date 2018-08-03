package com.suda.yzune.wakeupschedule.bean

import android.arch.lifecycle.MutableLiveData

data class CourseEditBean(
        var id: Int = -1,
        val time: MutableLiveData<TimeBean> =
                MutableLiveData<TimeBean>().apply {
                    this.value = TimeBean(1, 1, 2)
                },
        var room: String? = "",
        var teacher: String? = "",
        val weekList: MutableLiveData<ArrayList<Int>> =
                MutableLiveData<ArrayList<Int>>().apply {
                    this.value = ArrayList<Int>().apply {
                        for (i in 1..30) {
                            this.add(i)
                        }
                    }
                },
        var tableName: String = ""
)