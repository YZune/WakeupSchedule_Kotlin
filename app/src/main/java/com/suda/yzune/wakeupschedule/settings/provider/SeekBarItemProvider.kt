package com.suda.yzune.wakeupschedule.settings.provider

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.items.BaseSettingItem
import com.suda.yzune.wakeupschedule.settings.items.SeekBarItem
import com.suda.yzune.wakeupschedule.settings.items.SettingType
import splitties.dimensions.dip

class SeekBarItemProvider : BaseItemProvider<BaseSettingItem>() {

    override val itemViewType: Int
        get() = SettingType.SEEKBAR

    override val layoutId: Int
        get() = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LinearLayout(context).apply {
            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
            setPadding(dip(16), 0, dip(16), 0)
            addView(TextView(context).apply {
                id = R.id.anko_text_view
                textSize = 16f
                gravity = Gravity.CENTER_VERTICAL
            }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                marginEnd = dip(16)
                weight = 1f
            })

            addView(TextView(context).apply {
                id = R.id.anko_tv_prefix
                textSize = 12f
                gravity = Gravity.CENTER_VERTICAL
            }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                marginEnd = dip(4)
            })

            addView(TextView(context).apply {
                id = R.id.anko_tv_value
                textSize = 12f
                gravity = Gravity.CENTER_VERTICAL
            }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT))

            addView(TextView(context).apply {
                id = R.id.anko_tv_unit
                textSize = 12f
                gravity = Gravity.CENTER_VERTICAL
            }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                marginStart = dip(4)
            })

        }
        view.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        view.dip(64))
        return BaseViewHolder(view)
    }

    override fun convert(helper: BaseViewHolder, data: BaseSettingItem?) {
        if (data == null) return
        val item = data as SeekBarItem
        helper.setText(R.id.anko_text_view, item.title)
        if (item.valueInt > item.max || item.valueInt < item.min) {
            helper.setText(R.id.anko_tv_value, "无效值")
            helper.setGone(R.id.anko_tv_unit, true)
            helper.setGone(R.id.anko_tv_prefix, true)
            return
        } else {
            helper.setText(R.id.anko_tv_value, "${item.valueInt}")
            helper.setGone(R.id.anko_tv_unit, false)
            helper.setGone(R.id.anko_tv_prefix, false)
        }
        helper.setText(R.id.anko_tv_unit, item.unit)
        if (data.prefix.isNotEmpty()) {
            helper.setGone(R.id.anko_tv_prefix, false)
            helper.setText(R.id.anko_tv_prefix, data.prefix)
        } else {
            helper.setGone(R.id.anko_tv_prefix, true)
        }
    }

}