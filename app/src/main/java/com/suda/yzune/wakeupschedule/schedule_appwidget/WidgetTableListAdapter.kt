package com.suda.yzune.wakeupschedule.schedule_appwidget

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableSelectBean

class WidgetTableListAdapter(layoutResId: Int, data: MutableList<TableSelectBean>) :
        BaseQuickAdapter<TableSelectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TableSelectBean?) {
        if (item == null) return
        helper.setVisible(R.id.ib_share, false)
        helper.setVisible(R.id.ib_edit, false)
        helper.setVisible(R.id.ib_delete, false)

        if (item.tableName != "") {
            helper.setText(R.id.tv_table_name, item.tableName)
        } else {
            helper.setText(R.id.tv_table_name, "我的课表")
        }
        val imageView = helper.getView<ImageView>(R.id.iv_pic)
        if (item.background != "") {
            Glide.with(context)
                    .load(item.background)
                    .override(400, 600)
                    .into(imageView)
        } else {
            Glide.with(context)
                    .load(R.drawable.main_background_2020_1)
                    .override(400, 600)
                    .into(imageView)
        }
    }
}