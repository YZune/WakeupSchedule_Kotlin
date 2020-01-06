package com.suda.yzune.wakeupschedule.schedule_import

import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

abstract class CommonImport {

    val baseList = arrayListOf<CourseBaseBean>()
    val detailList = arrayListOf<CourseDetailBean>()


}