package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.CategoryItem
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
                    backgroundColorResource = R.color.colorAccent
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

    override fun onBindViewHolder(holder: ViewHolder, category: CategoryItem) {
        holder.tvCategory.text = category.name
        if (category.hasMarginTop) {
            holder.vTop.visibility = View.VISIBLE
        } else {
            holder.vTop.visibility = View.GONE
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCategory: TextView = itemView.find(R.id.anko_text_view)
        var vTop: View = itemView.find(R.id.anko_view)
    }

}