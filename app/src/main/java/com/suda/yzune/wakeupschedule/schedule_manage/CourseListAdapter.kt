package com.suda.yzune.wakeupschedule.schedule_manage

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.card.MaterialCardView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import splitties.resources.styledColor

class CourseListAdapter(layoutResId: Int, data: MutableList<CourseBaseBean>) :
        BaseQuickAdapter<CourseBaseBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: CourseBaseBean?) {
        if (item == null) return
        helper.getView<MaterialCardView>(R.id.cv_course).setCardBackgroundColor(ColorUtils.blendARGB(context.styledColor(R.attr.colorSurface), Color.parseColor(item.color), 0.32f))
        helper.setText(R.id.tv_course_name, item.courseName)
    }
}