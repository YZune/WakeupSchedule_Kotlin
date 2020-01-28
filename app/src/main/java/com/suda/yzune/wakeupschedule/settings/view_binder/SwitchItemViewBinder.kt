package com.suda.yzune.wakeupschedule.settings.view_binder

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.SwitchItem
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import splitties.dimensions.dip


class SwitchItemViewBinder constructor(private val onCheckItemCheckChange: (SwitchItem, Boolean) -> Unit) : ItemViewBinder<SwitchItem, SwitchItemViewBinder.ViewHolder>() {

    @SuppressLint("RestrictedApi")
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = LinearLayout(parent.context).apply {
            id = R.id.anko_layout
            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
            // lparams(matchParent, dip(64))
            addView(TextView(context).apply {
                id = R.id.anko_text_view
                textSize = 16f
            }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_VERTICAL
                marginStart = dip(16)
                weight = 1f
            })
            val checkBox = AppCompatCheckBox(context).apply {
                id = R.id.anko_check_box
                val color = PreferenceUtils.getIntFromSP(context, "nav_bar_color", ContextCompat.getColor(context, R.color.colorAccent))
                val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
                val colors = intArrayOf(color, Color.GRAY)
                supportButtonTintList = ColorStateList(states, colors)
            }

            addView(checkBox, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_VERTICAL
                marginEnd = dip(8)
            })
        }
        view.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        view.dip(64))
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SwitchItem) {
        holder.setIsRecyclable(false)
        holder.tvTitle.text = item.title
        holder.checkbox.isChecked = item.checked
        holder.checkbox.setOnCheckedChangeListener { _, isChecked -> onCheckItemCheckChange.invoke(item, isChecked) }
        holder.layout.setOnClickListener {
            holder.checkbox.isChecked = !holder.checkbox.isChecked
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.anko_text_view)
        val checkbox: CheckBox = itemView.findViewById(R.id.anko_check_box)
        val layout: LinearLayout = itemView.findViewById(R.id.anko_layout)
    }
}