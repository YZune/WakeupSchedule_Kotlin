package com.suda.yzune.wakeupschedule.apply_info

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.HtmlCountBean

class ApplyInfoAdapter(layoutResId: Int, data: MutableList<HtmlCountBean>) :
        BaseQuickAdapter<HtmlCountBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: HtmlCountBean?) {
        if (item == null) return
        helper.setVisible(R.id.ll_detail, true)
        helper.setVisible(R.id.ll_detail_num, true)
        helper.setVisible(R.id.v_line, true)
        helper.setText(R.id.tv_school, item.school)
        helper.setText(R.id.tv_count, item.count.toString())
        helper.setText(R.id.tv_checked, item.checked.toString())
        helper.setText(R.id.tv_valid, item.valid.toString())
    }

}