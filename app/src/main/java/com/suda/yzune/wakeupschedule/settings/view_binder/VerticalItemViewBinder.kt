package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.VerticalItem
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import org.jetbrains.anko.*

class VerticalItemViewBinder constructor(
        private val onVerticalItemClickListener: (VerticalItem) -> Unit,
        private val onVerticalItemLongClickListener: (VerticalItem) -> Boolean
) : ItemViewBinder<VerticalItem, VerticalItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = AnkoContext.create(parent.context).apply {
            verticalLayout {
                id = R.id.anko_layout

                val outValue = TypedValue()
                context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
                backgroundResource = outValue.resourceId

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
                    topMargin = dip(4)
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
        if (item.isSpanned) {
            holder.tvDescription.text = ViewUtils.getHtmlSpannedString(item.description)
        } else {
            holder.tvDescription.text = item.description
        }
        holder.llVerticalItem.setOnClickListener { onVerticalItemClickListener.invoke(item) }
        holder.llVerticalItem.setOnLongClickListener { onVerticalItemLongClickListener.invoke(item) }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.find(R.id.anko_text_view)
        val tvDescription: TextView = itemView.find(R.id.anko_tv_description)
        val llVerticalItem: LinearLayout = itemView.find(R.id.anko_layout)
    }

}