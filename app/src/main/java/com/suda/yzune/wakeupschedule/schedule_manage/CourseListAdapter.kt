package com.suda.yzune.wakeupschedule.schedule_manage

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean

class CourseListAdapter(layoutResId: Int, data: MutableList<CourseBaseBean>) :
        BaseQuickAdapter<CourseBaseBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseBaseBean?) {
        if (item == null) return
        helper.setText(R.id.tv_course_name, item.courseName)
    }
}