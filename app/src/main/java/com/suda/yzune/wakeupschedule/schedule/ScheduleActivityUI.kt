package com.suda.yzune.wakeupschedule.schedule

import android.graphics.Color
import android.graphics.Typeface
import android.support.constraint.ConstraintSet.PARENT_ID
import android.widget.ImageView
import com.suda.yzune.wakeupschedule.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.support.v4.drawerLayout

class ScheduleActivityUI : AnkoComponent<ScheduleActivity> {

    override fun createView(ui: AnkoContext<ScheduleActivity>) = ui.apply {
        drawerLayout {

            constraintLayout {

                imageView {
                    id = R.id.anko_iv_bg
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams {
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID
                }

                textView {
                    id = R.id.anko_tv_date
                    textColor = Color.BLACK
                    textSize = 24f
                    typeface = Typeface.DEFAULT_BOLD
                }.lparams {
                    startToStart = PARENT_ID
                    topToTop = PARENT_ID
                    marginStart = dip(24)
                    topMargin = dip(24)
                }

                textView {
                    id = R.id.anko_tv_week
                    textColor = Color.BLACK
                }.lparams {
                    startToStart = PARENT_ID
                    topToTop = PARENT_ID
                    marginStart = dip(24)
                    topMargin = dip(24)
                }

                imageButton(R.drawable.main_nav) {
                    backgroundResource = R.attr.selectableItemBackgroundBorderless
                    padding = dip(4)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams(dip(32), dip(32)) {
                    topMargin = dip(48)
                    endToStart = R.id.anko_tv_date
                    topToTop = PARENT_ID
                }
            }
        }
    }.view
}