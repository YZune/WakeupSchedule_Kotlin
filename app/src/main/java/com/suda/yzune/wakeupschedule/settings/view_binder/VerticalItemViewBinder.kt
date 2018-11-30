package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.VerticalItem
import me.drakeet.multitype.ItemViewBinder
import org.jetbrains.anko.*

class VerticalItemViewBinder constructor(private val onVerticalItemClickListener: (VerticalItem) -> Unit) : ItemViewBinder<VerticalItem, VerticalItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = AnkoContext.create(parent.context).apply {
            verticalLayout {
                id = R.id.anko_layout
                lparams(matchParent, wrapContent)
                textView {
                    id = R.id.anko_text_view
                    textColor = Color.BLACK
                    textSize = 16f
                }.lparams(wrapContent, wrapContent) {
                    topMargin = dip(16)
                    marginStart = dip(16)
                    marginEnd = dip(16)
                }
                textView {
                    id = R.id.anko_tv_description
                    textSize = 12f
                }.lparams(wrapContent, wrapContent) {
                    bottomMargin = dip(16)
                    marginStart = dip(16)
                    marginEnd = dip(16)
                }
            }
        }.view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VerticalItem) {
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description
        holder.llVerticalItem.setOnClickListener { onVerticalItemClickListener.invoke(item) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.find(R.id.anko_text_view)
        val tvDescription: TextView = itemView.find(R.id.anko_tv_description)
        val llVerticalItem: LinearLayout = itemView.find(R.id.anko_layout)
    }

}