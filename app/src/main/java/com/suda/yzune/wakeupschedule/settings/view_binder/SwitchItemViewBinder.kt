package com.suda.yzune.wakeupschedule.settings.view_binder

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.SwitchItem
import me.drakeet.multitype.ItemViewBinder
import org.jetbrains.anko.*

class SwitchItemViewBinder constructor(private val onCheckItemCheckChange: (SwitchItem, Boolean) -> Unit) : ItemViewBinder<SwitchItem, SwitchItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): SwitchItemViewBinder.ViewHolder {
        val view = AnkoContext.create(parent.context).apply {
            val outValue = TypedValue()
            parent.context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
            linearLayout {
                lparams(matchParent, dip(64))
                backgroundResource = outValue.resourceId
                textView { }
            }
        }.view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SwitchItemViewBinder.ViewHolder, item: SwitchItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}