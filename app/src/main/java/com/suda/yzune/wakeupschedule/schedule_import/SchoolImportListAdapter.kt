package com.suda.yzune.wakeupschedule.schedule_import

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule_import.bean.SchoolInfo
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import splitties.dimensions.dip

class SchoolImportListAdapter(layoutResId: Int, data: MutableList<SchoolInfo>) :
        BaseQuickAdapter<SchoolInfo, BaseViewHolder>(layoutResId, data),
        StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private val map = mapOf(
            Common.TYPE_ZF to "正方教务",
            Common.TYPE_ZF_1 to "正方教务 1",
            Common.TYPE_ZF_NEW to "新正方教务",
            Common.TYPE_URP to "URP 系统",
            Common.TYPE_URP_NEW to "新 URP 系统",
            Common.TYPE_QZ to "强智教务 0",
            Common.TYPE_QZ_OLD to "旧强智教务",
            Common.TYPE_QZ_CRAZY to "强智教务 3",
            Common.TYPE_QZ_BR to "强智教务 1",
            Common.TYPE_QZ_WITH_NODE to "强智教务 2",
            Common.TYPE_CF to "乘方教务",
            Common.TYPE_PKU to "", // 北京大学
            Common.TYPE_BNUZ to "", // 北京师范大学珠海分校
            Common.TYPE_HNIU to "", // 湖南信息职业技术学院
            Common.TYPE_HNUST to "", // 湖南科技大学
            Common.TYPE_JNU to "by @Jiuh-star", // 暨南大学
            Common.TYPE_LOGIN to "", // 模拟登录方式
            Common.TYPE_MAINTAIN to "不可用" // 维护状态，暂不可用
    )

    private val thanksMap = mapOf(
            "华中科技大学" to "Lyt99",
            "清华大学" to "RikaSugisawa",
            "上海大学" to "Deep Sea",
            "吉林大学" to "颩欥殘膤",
            "西北工业大学" to "ludoux"
    )

    override fun getHeaderId(position: Int): Long {
        return getItem(position)!!.sortKey[0].toLong()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_school_name_head, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mHead = holder.itemView.findViewById<AppCompatTextView>(R.id.mHead)
        mHead.text = getItem(position)!!.sortKey
        val myGrad = mHead.background as GradientDrawable
        myGrad.setColor(getCustomizedColor(position % 9))
    }

    override fun convert(helper: BaseViewHolder, item: SchoolInfo?) {
        if (item == null) return
        helper.setGone(R.id.v_line, true)
        helper.setVisible(R.id.tv_top, false)
        helper.setVisible(R.id.tv_bottom, false)
        (helper.getView<View>(R.id.ll_detail).layoutParams as LinearLayoutCompat.LayoutParams)
                .marginEnd = context.dip(16)
        helper.setText(R.id.tv_school, item.name)
        if (item.type == Common.TYPE_LOGIN) {
            helper.setText(R.id.tv_center, "by @${thanksMap[item.name]}")
        } else if (item.sortKey != "通" && item.type != Common.TYPE_HELP) {
            helper.setText(R.id.tv_center, map[item.type])
        } else {
            helper.setText(R.id.tv_center, "")
        }
    }

    private fun getCustomizedColor(index: Int): Int {
        val customizedColors = context.resources.getIntArray(R.array.customizedColors)
        return customizedColors[index]
    }

}