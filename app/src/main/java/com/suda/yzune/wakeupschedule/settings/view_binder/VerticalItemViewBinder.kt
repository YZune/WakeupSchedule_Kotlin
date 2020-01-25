package com.suda.yzune.wakeupschedule.settings.view_binder

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
import splitties.dimensions.dip
import splitties.views.bottomPadding
import splitties.views.dsl.core.*
import splitties.views.topPadding

class VerticalItemViewBinder constructor(
        private val onVerticalItemClickListener: (VerticalItem) -> Unit,
        private val onVerticalItemLongClickListener: (VerticalItem) -> Boolean
) : ItemViewBinder<VerticalItem, VerticalItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = parent.verticalLayout(R.id.anko_layout) {

            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
            topPadding = dip(16)
            bottomPadding = dip(16)

            // lparams(matchParent, wrapContent)
            add(textView {
                id = R.id.anko_text_view
                textSize = 16f
            }, lParams(wrapContent, wrapContent) {
                marginStart = dip(16)
                marginEnd = dip(16)
            })

            add(textView {
                id = R.id.anko_tv_description
                textSize = 12f
            }, lParams(wrapContent, wrapContent) {
                topMargin = dip(4)
                marginStart = dip(16)
                marginEnd = dip(16)
            })
        }
        view.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VerticalItem) {
        holder.tvTitle.text = item.title
        holder.llVerticalItem.setOnClickListener { onVerticalItemClickListener.invoke(item) }
        holder.llVerticalItem.setOnLongClickListener { onVerticalItemLongClickListener.invoke(item) }
        if (item.description.isEmpty()) {
            holder.tvDescription.visibility = View.GONE
        } else {
            holder.tvDescription.visibility = View.VISIBLE
            if (item.isSpanned) {
                holder.tvDescription.text = ViewUtils.getHtmlSpannedString(item.description)
            } else {
                holder.tvDescription.text = item.description
            }
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.anko_text_view)
        val tvDescription: TextView = itemView.findViewById(R.id.anko_tv_description)
        val llVerticalItem: LinearLayout = itemView.findViewById(R.id.anko_layout)
    }

}