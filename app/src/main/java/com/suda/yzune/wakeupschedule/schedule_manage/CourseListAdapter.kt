package com.suda.yzune.wakeupschedule.schedule_manage

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean

class CourseListAdapter(layoutResId: Int, data: List<CourseBaseBean>) :
        BaseItemDraggableAdapter<CourseBaseBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseBaseBean) {
        helper.setText(R.id.tv_course_name, item.courseName)
        try {
            helper.getView<ImageView>(R.id.iv_color).imageTintList = ColorStateList.valueOf(Color.parseColor(item.color))
        } catch (e: Exception) {

        }
        helper.addOnClickListener(R.id.ib_edit)
        helper.addOnClickListener(R.id.ib_delete)
        helper.addOnLongClickListener(R.id.ib_delete)
    }
}