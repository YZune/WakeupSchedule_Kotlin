package com.suda.yzune.wakeupschedule.course_add

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.SizeUtils
import kotlinx.android.synthetic.main.activity_main.*

class SelectWeekActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_week)
        showWeeks()
    }

    private fun showWeeks() {
        val result = arrayListOf<Int>()
        val context = ll_week.context
        val margin = SizeUtils.dp2px(context, 4f)
        val textViewSize = SizeUtils.dp2px(context, 32f)
        val llHeight = SizeUtils.dp2px(context, 40f)
        for (i in 0 until 5) {
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            ll_week.addView(linearLayout)
            val params = linearLayout.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = llHeight
            linearLayout.layoutParams = params

            for (j in 0..5) {
                val week = i * 6 + j + 1
                val textView = TextView(context)
                val textParams = LinearLayout.LayoutParams(textViewSize, textViewSize)
                textParams.setMargins(margin, margin, margin, margin)
                textView.layoutParams = textParams
                textView.text = "$week"
                textView.gravity = Gravity.CENTER
                if (week in result){
                    textView.setTextColor(resources.getColor(R.color.white))
                    textView.background = ContextCompat.getDrawable(context, R.drawable.week_selected_bg)
                }
                else{
                    textView.setTextColor(resources.getColor(R.color.black))
                    textView.background = null
                }

                textView.setOnClickListener {
                    if (textView.background == null){
                        result.add(week)
                        textView.setTextColor(resources.getColor(R.color.white))
                        textView.background = ContextCompat.getDrawable(context, R.drawable.week_selected_bg)
                    }
                    else{
                        result.remove(week)
                        textView.setTextColor(resources.getColor(R.color.black))
                        textView.background = null
                    }
                    Log.d("输出",result.toString())
                }
                textView.setLines(1)
                linearLayout.addView(textView)
                //selections[week - 1] = textView
            }
        }
    }

}
