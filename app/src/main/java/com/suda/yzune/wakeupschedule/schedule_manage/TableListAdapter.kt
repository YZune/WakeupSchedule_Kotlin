package com.suda.yzune.wakeupschedule.schedule_manage

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableSelectBean

class TableListAdapter(layoutResId: Int, data: List<TableSelectBean>) :
        BaseQuickAdapter<TableSelectBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: TableSelectBean) {
        if (item.type == 1) {
            helper.getView<View>(R.id.ib_delete).visibility = View.GONE
        } else {
            helper.getView<View>(R.id.ib_delete).visibility = View.VISIBLE
        }

        if (item.tableName != "") {
            helper.setText(R.id.tv_table_name, item.tableName)
        } else {
            helper.setText(R.id.tv_table_name, "我的课表")
        }
        val imageView = helper.getView<ImageView>(R.id.iv_pic)
        if (item.background != "") {
            Glide.with(mContext)
                    .load(item.background)
                    .override(400, 600)
                    .into(imageView)
        } else {
            Glide.with(mContext)
                    .load(R.drawable.main_background_2019)
                    .override(400, 600)
                    .into(imageView)
        }

        helper
                .addOnClickListener(R.id.ib_share)
                .addOnClickListener(R.id.ib_edit)
                .addOnClickListener(R.id.ib_delete)
                .addOnLongClickListener(R.id.ib_delete)
    }
}