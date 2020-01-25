package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.CategoryItem
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import splitties.dimensions.dip
import splitties.views.backgroundColor
import splitties.views.dsl.core.*
import splitties.views.lines

class CategoryItemViewBinder : ItemViewBinder<CategoryItem, CategoryItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = parent.verticalLayout(R.id.anko_layout) {

            add(view(::View, R.id.anko_view)
                    , lParams(matchParent, ViewUtils.getStatusBarHeight(context) + dip(48)))

            add(horizontalLayout {
                setPadding(dip(16), dip(2), dip(16), dip(2))
                backgroundColor = PreferenceUtils.getIntFromSP(context, "nav_bar_color", ContextCompat.getColor(context, R.color.colorAccent))

                add(textView {
                    id = R.id.anko_text_view
                    textSize = 12f
                    lines = 1
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER_VERTICAL
                    typeface = Typeface.DEFAULT_BOLD
                }, lParams(wrapContent, wrapContent))

            }, lParams(wrapContent, wrapContent) {
                topMargin = dip(16)
            })
        }
        view.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CategoryItem) {
        holder.tvCategory.text = item.name
        if (item.hasMarginTop) {
            holder.vTop.visibility = View.VISIBLE
        } else {
            holder.vTop.visibility = View.GONE
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var tvCategory: TextView = itemView.findViewById(R.id.anko_text_view)
        var vTop: View = itemView.findViewById(R.id.anko_view)
    }

}