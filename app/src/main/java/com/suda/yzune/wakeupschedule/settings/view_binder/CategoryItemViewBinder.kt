package com.suda.yzune.wakeupschedule.settings.view_binder

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.CategoryItem
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import splitties.dimensions.dip

class CategoryItemViewBinder : ItemViewBinder<CategoryItem, CategoryItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = LinearLayout(parent.context).apply {
            id = R.id.anko_layout
            orientation = LinearLayout.VERTICAL
            addView(View(context).apply {
                id = R.id.anko_view
            }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewUtils.getStatusBarHeight(context) + dip(48)))

            addView(LinearLayout(context).apply {
                setPadding(dip(16), dip(2), dip(16), dip(2))
                setBackgroundColor(PreferenceUtils.getIntFromSP(context, "nav_bar_color", ContextCompat.getColor(context, R.color.colorAccent)))

                addView(TextView(context).apply {
                    id = R.id.anko_text_view
                    textSize = 12f
                    setLines(1)
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER_VERTICAL
                    typeface = Typeface.DEFAULT_BOLD
                }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))

            }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
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