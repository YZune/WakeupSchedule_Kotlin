package com.suda.yzune.wakeupschedule.settings.view_binder

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.HorizontalItem
import splitties.dimensions.dip

class HorizontalItemViewBinder constructor(private val onHorizontalItemClickListener: (HorizontalItem) -> Unit) : ItemViewBinder<HorizontalItem, HorizontalItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = LinearLayoutCompat(parent.context).apply {
            id = R.id.anko_layout
            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)

            gravity = Gravity.CENTER_VERTICAL

            addView(AppCompatTextView(context).apply {
                id = R.id.anko_text_view
                textSize = 16f
                gravity = Gravity.CENTER_VERTICAL
                setLines(1)
            }, LinearLayoutCompat.LayoutParams(0, LinearLayoutCompat.LayoutParams.WRAP_CONTENT).apply {
                marginStart = dip(16)
                marginEnd = dip(16)
                weight = 1f
            })

            addView(AppCompatTextView(context).apply {
                id = R.id.anko_tv_value
                gravity = Gravity.CENTER_VERTICAL
                textSize = 12f
            }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT).apply {
                marginStart = dip(16)
                marginEnd = dip(16)
            })
        }
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.dip(64))
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: HorizontalItem) {
        holder.tvTitle.text = item.title
        holder.tvValue.text = item.value
        holder.llItem.setOnClickListener { onHorizontalItemClickListener.invoke(item) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: AppCompatTextView = itemView.findViewById(R.id.anko_text_view)
        val tvValue: AppCompatTextView = itemView.findViewById(R.id.anko_tv_value)
        val llItem: LinearLayoutCompat = itemView.findViewById(R.id.anko_layout)
    }

}