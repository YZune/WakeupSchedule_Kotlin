package com.suda.yzune.wakeupschedule.schedule

import android.widget.ImageView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableSelectBean

class TableNameAdapter(layoutResId: Int, data: List<TableSelectBean>) :
        BaseItemDraggableAdapter<TableSelectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TableSelectBean) {
        if (item.type == 1) {
            helper.setVisible(R.id.iv_selected, true)
        } else {
            helper.setVisible(R.id.iv_selected, false)
        }

        if (item.tableName != "") {
            helper.setText(R.id.tv_table_name, item.tableName)
        } else {
            helper.setText(R.id.tv_table_name, "我的课表")
        }
        val imageView = helper.getView<ImageView>(R.id.iv_table_bg)
        if (item.background != "") {
            GlideApp.with(mContext.applicationContext)
                    .load(item.background)
                    .override(200, 300)
                    .into(imageView)
        } else {
            GlideApp.with(mContext.applicationContext)
                    .load(R.drawable.main_background_2019)
                    .override(200, 300)
                    .into(imageView)
        }
    }

}