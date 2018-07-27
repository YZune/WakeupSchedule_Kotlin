package com.suda.yzune.wakeupschedule.course_add

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

class AddCourseAdapter(layoutResId: Int, data: MutableList<CourseDetailBean>) :
        BaseItemDraggableAdapter<CourseDetailBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseDetailBean) {
        helper.setText(R.id.tv_item_id, "${helper.layoutPosition + 1}")
    }

}