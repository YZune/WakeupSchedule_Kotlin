package com.suda.yzune.wakeupschedule.schedule

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.design.widget.NavigationView
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewManager
import android.widget.ImageView
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper
import com.suda.yzune.wakeupschedule.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.viewPager


class ScheduleActivityUI : AnkoComponent<ScheduleActivity> {

    private inline fun ViewManager.verticalSeekBarWrapper(init: com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper.() -> Unit) = ankoView({ VerticalSeekBarWrapper(it) }, theme = 0) { init() }
    private inline fun ViewManager.verticalSeekBar(init: com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar.() -> Unit) = ankoView({ VerticalSeekBar(it) }, theme = 0) { init() }
    private inline fun ViewManager.navigationView(init: android.support.design.widget.NavigationView.() -> Unit) = ankoView({ NavigationView(it) }, theme = 0) { init() }
    private inline fun ViewManager.recyclerView(init: android.support.v7.widget.RecyclerView.() -> Unit) = ankoView({ RecyclerView(it) }, theme = 0) { init() }

    override fun createView(ui: AnkoContext<ScheduleActivity>) = ui.apply {
        drawerLayout {
            id = R.id.anko_drawer_layout

            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

            constraintLayout {
                id = R.id.anko_cl_schedule

                imageView {
                    id = R.id.anko_iv_bg
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams(matchParent, matchParent) {
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
                    topMargin = dip(48)
                }

                textView {
                    id = R.id.anko_tv_week
                    textColor = Color.BLACK
                }.lparams {
                    startToStart = R.id.anko_tv_date
                    topToBottom = R.id.anko_tv_date
                    topMargin = dip(4)
                }

                textView {
                    id = R.id.anko_tv_weekday
                    textColor = Color.BLACK
                }.lparams {
                    startToEnd = R.id.anko_tv_week
                    topToBottom = R.id.anko_tv_date
                    topMargin = dip(4)
                    marginStart = dip(8)
                }

                imageButton(R.drawable.main_nav) {
                    id = R.id.anko_ib_nav
                    backgroundResource = outValue.resourceId
                    padding = dip(4)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams(dip(32), dip(32)) {
                    topMargin = dip(48)
                    endToStart = R.id.anko_tv_date
                    topToTop = PARENT_ID
                }

                imageButton(R.drawable.schedule_add) {
                    id = R.id.anko_ib_add
                    backgroundResource = outValue.resourceId
                    padding = dip(4)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams(dip(32), dip(32)) {
                    topMargin = dip(48)
                    endToStart = R.id.anko_ib_import
                    topToTop = PARENT_ID
                }

                imageButton(R.drawable.schedule_import) {
                    id = R.id.anko_ib_import
                    backgroundResource = outValue.resourceId
                    padding = dip(4)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams(dip(32), dip(32)) {
                    topMargin = dip(48)
                    endToStart = R.id.anko_ib_more
                    topToTop = PARENT_ID
                }

                imageButton(R.drawable.more) {
                    id = R.id.anko_ib_more
                    backgroundResource = outValue.resourceId
                    padding = dip(4)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams(dip(32), dip(32)) {
                    topMargin = dip(48)
                    marginEnd = dip(8)
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
                }

                viewPager {
                    id = R.id.anko_vp_schedule
                }.lparams(matchParent, 0) {
                    topToBottom = R.id.anko_tv_week
                    bottomToBottom = PARENT_ID
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    verticalSeekBarWrapper {
                        verticalSeekBar {
                            id = R.id.anko_sb_week
                            progressDrawable = context.getDrawable(R.color.transparent)
                            splitTrack = false
                            thumb = context.getDrawable(R.color.transparent)
                        }
                    }.lparams(wrapContent, 0) {
                        topToBottom = R.id.anko_tv_week
                        bottomToBottom = PARENT_ID
                        endToEnd = PARENT_ID
                        topMargin = dip(192)
                        bottomMargin = dip(48)
                    }
                }
            }

            navigationView {
                id = R.id.anko_nv
                fitsSystemWindows = true
                inflateHeaderView(R.layout.nav_header)
                inflateMenu(R.menu.main_navigation_menu)
            }.lparams(matchParent, matchParent) {
                gravity = Gravity.START
            }

            navigationView {
                id = R.id.anko_nv_end
                fitsSystemWindows = true
                recyclerView {
                    id = R.id.anko_rv_table_name
                    isNestedScrollingEnabled = false
                }
            }.lparams(dip(96), matchParent) {
                gravity = Gravity.END
            }
        }
    }.view
}