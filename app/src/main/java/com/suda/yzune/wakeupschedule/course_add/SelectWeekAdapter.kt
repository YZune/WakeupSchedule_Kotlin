package com.suda.yzune.wakeupschedule.course_add

import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R

class SelectWeekAdapter(layoutResId: Int, max: Int, private val intData: List<Int>) :
        BaseQuickAdapter<Int, BaseViewHolder>(layoutResId, (1..max).toList()) {

    override fun convert(helper: BaseViewHolder, item: Int) {
        helper.setText(R.id.tv_num, "$item")
        if (intData.contains(item)) {
            helper.setTextColor(R.id.tv_num, Color.WHITE)
            helper.setBackgroundRes(R.id.tv_num, R.drawable.week_selected_bg)
        } else {
            helper.setTextColor(R.id.tv_num, Color.BLACK)
            helper.getView<TextView>(R.id.tv_num).background = null
        }
    }
}