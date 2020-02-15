package com.suda.yzune.wakeupschedule.settings.provider

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.items.BaseSettingItem
import com.suda.yzune.wakeupschedule.settings.items.SettingType
import com.suda.yzune.wakeupschedule.settings.items.SwitchItem
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.getPrefer
import splitties.dimensions.dip
import splitties.resources.color

class SwitchItemProvider : BaseItemProvider<BaseSettingItem>() {

    override val itemViewType: Int
        get() = SettingType.SWITCH

    override val layoutId: Int
        get() = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = ConstraintLayout(context).apply {
            id = R.id.anko_layout
            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
            setPadding(dip(16), dip(16), 0, dip(16))

            addView(AppCompatTextView(context).apply {
                id = R.id.anko_text_view
                textSize = 16f
            }, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                startToStart = ConstraintSet.PARENT_ID
                endToStart = R.id.anko_check_box
                topToTop = ConstraintSet.PARENT_ID
                bottomToTop = R.id.anko_tv_description
                marginEnd = dip(16)
            })

            addView(AppCompatTextView(context).apply {
                id = R.id.anko_tv_description
                textSize = 12f
            }, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dip(4)
                startToStart = ConstraintSet.PARENT_ID
                endToStart = R.id.anko_check_box
                topToBottom = R.id.anko_text_view
                bottomToBottom = ConstraintSet.PARENT_ID
                marginEnd = dip(16)
            })

            val checkBox = AppCompatCheckBox(context).apply {
                id = R.id.anko_check_box
                val color = context.getPrefer().getInt(Const.KEY_THEME_COLOR, color(R.color.colorAccent))
                val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
                val colors = intArrayOf(color, Color.GRAY)
                supportButtonTintList = ColorStateList(states, colors)
            }

            addView(checkBox, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, dip(32)).apply {
                endToEnd = ConstraintSet.PARENT_ID
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
            })
        }
        view.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        return BaseViewHolder(view)
    }

    override fun convert(helper: BaseViewHolder, data: BaseSettingItem?) {
        if (data == null) return
        val item = data as SwitchItem
        helper.setText(R.id.anko_text_view, item.title)
        helper.getView<AppCompatCheckBox>(R.id.anko_check_box).apply {
            isChecked = item.checked
        }
        if (data.desc.isEmpty()) {
            helper.setGone(R.id.anko_tv_description, true)
        } else {
            helper.setText(R.id.anko_tv_description, item.desc)
            helper.setGone(R.id.anko_tv_description, false)
        }

    }

}