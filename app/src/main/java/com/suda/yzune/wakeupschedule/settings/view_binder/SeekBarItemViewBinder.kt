package com.suda.yzune.wakeupschedule.settings.view_binder

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.drakeet.multitype.ItemViewBinder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.settings.bean.SeekBarItem
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import splitties.dimensions.dip

class SeekBarItemViewBinder constructor(private val onSeekValueChange: (SeekBarItem, Int) -> Unit) : ItemViewBinder<SeekBarItem, SeekBarItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): SeekBarItemViewBinder.ViewHolder {
        val view = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            addView(LinearLayout(context).apply {
                addView(TextView(context).apply {
                    id = R.id.anko_text_view
                    textSize = 16f
                    gravity = Gravity.CENTER_VERTICAL
                }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                    marginStart = dip(16)
                    marginEnd = dip(16)
                    weight = 1f
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
                    marginStart = dip(8)
                    marginEnd = dip(16)
                })

            }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dip(16)
            })

            addView(AppCompatSeekBar(context).apply {
                id = R.id.anko_seek_bar
                val color = PreferenceUtils.getIntFromSP(context, "nav_bar_color", ContextCompat.getColor(context, R.color.colorAccent))
                DrawableCompat.setTint(thumb, color)
                DrawableCompat.setTint(progressDrawable, color)
            }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
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
        val seekBar: AppCompatSeekBar = itemView.findViewById(R.id.anko_seek_bar)
    }
}