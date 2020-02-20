package com.suda.yzune.wakeupschedule.schedule

import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import splitties.dimensions.dip

class TableNameAdapter(layoutResId: Int, data: MutableList<TableSelectBean>) :
        BaseQuickAdapter<TableSelectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TableSelectBean?) {
        if (item == null) return
        helper.setGone(R.id.menu_setting, item.type != 1)

        if (item.tableName != "") {
            helper.setText(R.id.tv_table_name, item.tableName)
        } else {
            helper.setText(R.id.tv_table_name, "我的课表")
        }
        val imageView = helper.getView<AppCompatImageView>(R.id.iv_table_bg)
        if (item.background != "") {
            Glide.with(context)
                    .load(item.background)
                    .override(200, 300)
                    .transform(CenterCrop(), RoundedCorners(context.dip(4)))
                    .into(imageView)
        } else {
            Glide.with(context)
                    .load(R.drawable.main_background_2020_1)
                    .override(200, 300)
                    .transform(CenterCrop(), RoundedCorners(context.dip(4)))
                    .into(imageView)
        }
    }

}