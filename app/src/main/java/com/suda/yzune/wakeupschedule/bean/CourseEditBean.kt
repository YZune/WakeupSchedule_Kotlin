package com.suda.yzune.wakeupschedule.bean

import androidx.lifecycle.MutableLiveData

data class CourseEditBean(
        var id: Int = -1,
        val time: MutableLiveData<TimeBean> =
                MutableLiveData<TimeBean>().apply {
                    this.value = TimeBean(1, 1, 2)
                },
        var room: String? = "",
        var teacher: String? = "",
        val weekList: MutableLiveData<ArrayList<Int>>,
        var tableId: Int = 0
)