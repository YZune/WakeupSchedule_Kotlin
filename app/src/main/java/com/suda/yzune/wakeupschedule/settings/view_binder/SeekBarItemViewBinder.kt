package com.suda.yzune.wakeupschedule.settings.view_binder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.SeekBarItem
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import splitties.dimensions.dip
import splitties.views.dsl.core.*

class SeekBarItemViewBinder constructor(private val onSeekValueChange: (SeekBarItem, Int) -> Unit) : ItemViewBinder<SeekBarItem, SeekBarItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): SeekBarItemViewBinder.ViewHolder {
        val view = parent.verticalLayout {
            // lparams(matchParent, wrapContent)

            add(horizontalLayout {

                add(textView(R.id.anko_text_view) {
                    textSize = 16f
                    gravity = Gravity.CENTER_VERTICAL
                }, lParams(0, matchParent) {
                    marginStart = dip(16)
                    marginEnd = dip(16)
                    weight = 1f
                })

                add(textView {
                    id = R.id.anko_tv_value
                    textSize = 12f
                    gravity = Gravity.CENTER_VERTICAL
                }, lParams(wrapContent, matchParent))

                add(textView {
                    id = R.id.anko_tv_unit
                    textSize = 12f
                    gravity = Gravity.CENTER_VERTICAL
                }, lParams(wrapContent, matchParent) {
                    marginStart = dip(8)
                    marginEnd = dip(16)
                })

            }, lParams(matchParent, wrapContent) {
                topMargin = dip(16)
            })

            add(seekBar(R.id.anko_seek_bar) {
                val color = PreferenceUtils.getIntFromSP(context, "nav_bar_color", ContextCompat.getColor(context, R.color.colorAccent))
                DrawableCompat.setTint(thumb, color)
                DrawableCompat.setTint(progressDrawable, color)
            }, lParams(matchParent, wrapContent) {
                marginStart = dip(8)
                marginEnd = dip(8)
                topMargin = dip(8)
                bottomMargin = dip(16)
            })
        }
        view.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SeekBarItem) {
        //holder.setIsRecyclable(false)

        holder.tvTitle.text = item.title
        holder.seekBar.progress = item.valueInt - item.min
        holder.seekBar.max = item.max - item.min
        holder.tvValue.text = item.valueInt.toString()
        holder.tvUnit.text = item.unit
        holder.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    onSeekValueChange.invoke(item, progress)
                    holder.tvValue.text = "${progress + item.min}"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.anko_text_view)
        val tvUnit: TextView = itemView.findViewById(R.id.anko_tv_unit)
        val tvValue: TextView = itemView.findViewById(R.id.anko_tv_value)
        val seekBar: SeekBar = itemView.findViewById(R.id.anko_seek_bar)
    }
}