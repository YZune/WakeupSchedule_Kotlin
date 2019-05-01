package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.CategoryItem
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import me.drakeet.multitype.ItemViewBinder
import org.jetbrains.anko.*

class CategoryItemViewBinder : ItemViewBinder<CategoryItem, CategoryItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = AnkoContext.create(parent.context).apply {
            verticalLayout {
                id = R.id.anko_layout
                view {
                    id = R.id.anko_view
                }.lparams(matchParent, ViewUtils.getStatusBarHeight(context) + dip(48))

                linearLayout {
                    setPadding(dip(16), dip(2), dip(16), dip(2))
                    backgroundColor = PreferenceUtils.getIntFromSP(context, "nav_bar_color", ContextCompat.getColor(context, R.color.colorAccent))
                    textView {
                        id = R.id.anko_text_view
                        textColor = Color.WHITE
                        textSize = 12f
                        lines = 1
                        gravity = Gravity.CENTER_VERTICAL
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams(wrapContent, wrapContent)

                }.lparams(wrapContent, wrapContent) {
                    topMargin = dip(16)
                }
            }
        }.view
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
        var tvCategory: TextView = itemView.find(R.id.anko_text_view)
        var vTop: View = itemView.find(R.id.anko_view)
    }

}