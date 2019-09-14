package com.suda.yzune.wakeupschedule.schedule_manage

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean

class CourseListAdapter(layoutResId: Int, data: List<CourseBaseBean>) :
        BaseQuickAdapter<CourseBaseBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseBaseBean) {
        helper.setText(R.id.tv_course_name, item.courseName)
        helper.addOnClickListener(R.id.ib_edit)
        helper.addOnClickListener(R.id.ib_delete)
        helper.addOnLongClickListener(R.id.ib_delete)
    }
}