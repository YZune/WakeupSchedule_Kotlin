package com.suda.yzune.wakeupschedule.schedule_import

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule_import.bean.SchoolInfo
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter

class SchoolImportListAdapter(layoutResId: Int, data: MutableList<SchoolInfo>) :
        BaseQuickAdapter<SchoolInfo, BaseViewHolder>(layoutResId, data),
        StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

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
        helper.getView<View>(R.id.ll_detail).visibility = View.GONE
        helper.getView<View>(R.id.ll_detail_num).visibility = View.GONE
        helper.getView<View>(R.id.v_line).visibility = View.GONE
        helper.setText(R.id.tv_school, item.name)
    }

    private fun getCustomizedColor(index: Int): Int {
        val customizedColors = context.resources.getIntArray(R.array.customizedColors)
        return customizedColors[index]
    }

}