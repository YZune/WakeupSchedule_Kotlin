package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.SwitchItem
import me.drakeet.multitype.ItemViewBinder
import org.jetbrains.anko.*

class SwitchItemViewBinder constructor(private val onCheckItemCheckChange: (SwitchItem, Boolean) -> Unit) : ItemViewBinder<SwitchItem, SwitchItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): SwitchItemViewBinder.ViewHolder {
        val view = AnkoContext.create(parent.context).apply {
            linearLayout {
                id = R.id.anko_layout
                lparams(matchParent, dip(64))
                textView {
                    id = R.id.anko_text_view
                    textColor = Color.BLACK
                    textSize = 16f
                }.lparams(0, wrapContent) {
                    gravity = Gravity.CENTER_VERTICAL
                    marginStart = dip(16)
                    weight = 1f
                }
                switch {
                    id = R.id.anko_switch
                    switchMinWidth = dip(48)
                    setThumbResource(R.drawable.switch_selector)
                    setTrackResource(R.drawable.switch_track)
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.CENTER_VERTICAL
                    marginEnd = dip(16)
                }
            }
        }.view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SwitchItemViewBinder.ViewHolder, item: SwitchItem) {
        holder.tvTitle.text = item.title
        holder.switch.isChecked = item.checked
        holder.switch.setOnCheckedChangeListener { _, isChecked -> onCheckItemCheckChange.invoke(item, isChecked) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.anko_text_view)
        val switch: Switch = itemView.findViewById(R.id.anko_switch)
        val layout: LinearLayout = itemView.findViewById(R.id.anko_layout)
    }
}