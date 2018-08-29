package com.suda.yzune.wakeupschedule.settings

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean

class TimeSettingsAdapter(layoutResId: Int, data: List<TimeDetailBean>) :
        BaseItemDraggableAdapter<TimeDetailBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TimeDetailBean) {
        val name = if (item.node <= 16) "第 ${item.node} 节" else "第 ${item.node - 16} 节"
        helper.setText(R.id.tv_time_name, name)
        helper.setText(R.id.tv_start, item.startTime)
        helper.setText(R.id.tv_end, item.endTime)
    }
}