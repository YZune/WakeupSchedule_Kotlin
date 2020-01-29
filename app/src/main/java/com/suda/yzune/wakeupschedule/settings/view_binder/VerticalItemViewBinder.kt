package com.suda.yzune.wakeupschedule.settings.view_binder

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.VerticalItem
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import splitties.dimensions.dip

class VerticalItemViewBinder constructor(
        private val onVerticalItemClickListener: (VerticalItem) -> Unit,
        private val onVerticalItemLongClickListener: (VerticalItem) -> Boolean
) : ItemViewBinder<VerticalItem, VerticalItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = LinearLayoutCompat(parent.context).apply {
            id = R.id.anko_layout
            orientation = LinearLayoutCompat.VERTICAL
            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
            setPadding(0, dip(16), 0, dip(16))

            addView(AppCompatTextView(context).apply {
                id = R.id.anko_text_view
                textSize = 16f
            }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT).apply {
                marginStart = dip(16)
                marginEnd = dip(16)
            })

            addView(AppCompatTextView(context).apply {
                id = R.id.anko_tv_description
                textSize = 12f
            }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT).apply {
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
        val tvTitle: AppCompatTextView = itemView.findViewById(R.id.anko_text_view)
        val tvDescription: AppCompatTextView = itemView.findViewById(R.id.anko_tv_description)
        val llVerticalItem: LinearLayoutCompat = itemView.findViewById(R.id.anko_layout)
    }

}