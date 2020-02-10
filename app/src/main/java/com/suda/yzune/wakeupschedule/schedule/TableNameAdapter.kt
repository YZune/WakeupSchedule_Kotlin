package com.suda.yzune.wakeupschedule.schedule

import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import splitties.dimensions.dip

class TableNameAdapter(layoutResId: Int, data: MutableList<TableSelectBean>) :
        BaseQuickAdapter<TableSelectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TableSelectBean?) {
        if (item == null) return
        if (item.type == 1) {
            helper.setVisible(R.id.ll_action, true)
        } else {
            helper.setVisible(R.id.ll_action, false)
        }

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
                    .transform(RoundedCornersTransformation(context.dip(4), 0, RoundedCornersTransformation.CornerType.ALL))
                    .into(imageView)
        } else {
            Glide.with(context)
                    .load(R.drawable.main_background_2020_1)
                    .override(200, 300)
                    .transform(RoundedCornersTransformation(context.dip(4), 0, RoundedCornersTransformation.CornerType.ALL))
                    .into(imageView)
        }
    }

}