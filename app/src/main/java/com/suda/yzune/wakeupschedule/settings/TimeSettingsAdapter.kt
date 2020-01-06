package com.suda.yzune.wakeupschedule.settings

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import kotlinx.android.parcel.RawValue

class TimeSettingsAdapter(private val layoutResId: Int, val list: @RawValue List<TimeDetailBean>) :
        BaseQuickAdapter<TimeDetailBean, BaseViewHolder>(layoutResId, list) {

    override fun convert(helper: BaseViewHolder, item: TimeDetailBean) {
        val name = "第 ${item.node} 节"
        helper.setText(R.id.tv_time_name, name)
        helper.setText(R.id.tv_start, item.startTime)
        helper.setText(R.id.tv_end, item.endTime)
    }
}