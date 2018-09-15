package com.suda.yzune.wakeupschedule.schedule

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R

class TableNameAdapter(layoutResId: Int, data: List<String>) :
        BaseItemDraggableAdapter<String, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.tv_table_name, item)
    }

}