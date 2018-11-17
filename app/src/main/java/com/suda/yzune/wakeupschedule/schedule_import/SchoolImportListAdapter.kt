package com.suda.yzune.wakeupschedule.schedule_import

import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import org.jetbrains.anko.find

class SchoolImportListAdapter(layoutResId: Int, data: MutableList<SchoolListBean>) :
        BaseItemDraggableAdapter<SchoolListBean, BaseViewHolder>(layoutResId, data),
        StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    override fun getHeaderId(position: Int): Long {
        return getItem(position)!!.sortKey[0].toLong()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_school_name_head, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mHead = holder.itemView.find<TextView>(R.id.mHead)
        mHead.text = getItem(position)!!.sortKey
        val myGrad = mHead.background as GradientDrawable
        myGrad.setColor(getCustomizedColor(position % 9))
    }

    override fun convert(helper: BaseViewHolder, item: SchoolListBean) {
        helper.getView<View>(R.id.ll_detail).visibility = View.GONE
        helper.getView<View>(R.id.ll_detail_num).visibility = View.GONE
        helper.getView<View>(R.id.v_line).visibility = View.GONE
        helper.setText(R.id.tv_school, item.name)
    }

    private fun getCustomizedColor(index: Int): Int {
        val customizedColors = mContext.resources.getIntArray(R.array.customizedColors)
        return customizedColors[index]
    }

}