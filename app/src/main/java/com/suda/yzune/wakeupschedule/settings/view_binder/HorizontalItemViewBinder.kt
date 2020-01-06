package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.HorizontalItem
import org.jetbrains.anko.*

class HorizontalItemViewBinder constructor(private val onHorizontalItemClickListener: (HorizontalItem) -> Unit) : ItemViewBinder<HorizontalItem, HorizontalItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = AnkoContext.create(parent.context).apply {
            linearLayout {
                id = R.id.anko_layout

                val outValue = TypedValue()
                context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
                backgroundResource = outValue.resourceId

                gravity = Gravity.CENTER_VERTICAL
                lparams(matchParent, dip(64))

                textView {
                    id = R.id.anko_text_view
                    textSize = 16f
                    gravity = Gravity.CENTER_VERTICAL
                    lines = 1
                }.lparams(0, wrapContent) {
                    marginStart = dip(16)
                    marginEnd = dip(16)
                    weight = 1f
                }
                textView {
                    id = R.id.anko_tv_value
                    gravity = Gravity.CENTER_VERTICAL
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
        holder.llItem.setOnClickListener { onHorizontalItemClickListener.invoke(item) }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.find(R.id.anko_text_view)
        val tvValue: TextView = itemView.find(R.id.anko_tv_value)
        val llItem: LinearLayout = itemView.find(R.id.anko_layout)
    }

}