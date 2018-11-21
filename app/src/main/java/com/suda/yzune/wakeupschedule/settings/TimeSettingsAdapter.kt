package com.suda.yzune.wakeupschedule.settings

import android.os.Parcelable
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class TimeSettingsAdapter(private val layoutResId: Int, val list: @RawValue List<TimeDetailBean>) :
        BaseItemDraggableAdapter<TimeDetailBean, BaseViewHolder>(layoutResId, list), Parcelable {

    override fun convert(helper: BaseViewHolder, item: TimeDetailBean) {
        val name = "第 ${item.node} 节"
        helper.setText(R.id.tv_time_name, name)
        helper.setText(R.id.tv_start, item.startTime)
        helper.setText(R.id.tv_end, item.endTime)
    }
}