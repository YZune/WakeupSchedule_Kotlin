package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.HorizontalItem
import me.drakeet.multitype.ItemViewBinder
import org.jetbrains.anko.*

class HorizontalItemViewBinder constructor(private val onHorizontalItemClickListener: (HorizontalItem) -> Unit) : ItemViewBinder<HorizontalItem, HorizontalItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = AnkoContext.create(parent.context).apply {
            linearLayout {
                id = R.id.anko_layout
                lparams(matchParent, dip(64))
                textView {
                    id = R.id.anko_text_view
                    textColor = Color.BLACK
                    textSize = 16f
                    lines = 1
                }.lparams(0, wrapContent) {
                    marginStart = dip(16)
                    marginEnd = dip(16)
                    weight = 1f
                }
                textView {
                    id = R.id.anko_tv_value
                    textSize = 12f
                }.lparams(wrapContent, wrapContent) {
                    marginStart = dip(16)
                    marginEnd = dip(16)
                }
            }
        }.view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: HorizontalItem) {
        holder.tvTitle.text = item.title
        holder.tvValue.text = item.value
        holder.llVerticalItem.setOnClickListener { onHorizontalItemClickListener.invoke(item) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.find(R.id.anko_text_view)
        val tvValue: TextView = itemView.find(R.id.anko_tv_value)
        val llVerticalItem: LinearLayout = itemView.find(R.id.anko_layout)
    }

}