package com.suda.yzune.wakeupschedule.schedule

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.v4.content.res.ResourcesCompat
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewManager
import android.widget.ImageView
import com.github.mmin18.widget.RealtimeBlurView
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.viewPager


class ScheduleActivityUI : AnkoComponent<ScheduleActivity> {

    private inline fun ViewManager.verticalSeekBarWrapper(init: com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper.() -> Unit) = ankoView({ VerticalSeekBarWrapper(it) }, theme = 0) { init() }
    private inline fun ViewManager.verticalSeekBar(init: com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar.() -> Unit) = ankoView({ VerticalSeekBar(it) }, theme = 0) { init() }
    private inline fun ViewManager.blurLayout(init: com.github.mmin18.widget.RealtimeBlurView.() -> Unit) = ankoView({ RealtimeBlurView(it, null) }, theme = 0) { init() }

    override fun createView(ui: AnkoContext<ScheduleActivity>) = ui.apply {

        constraintLayout {

            val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)

            frameLayout {
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

                        //导航按钮
                        textView("\uE6A7") {
                            id = R.id.anko_ib_nav
                            backgroundResource = outValue.resourceId
                            textSize = 20f
                            gravity = Gravity.CENTER
                            includeFontPadding = false
                            setTypeface(iconFont, Typeface.BOLD)
                        }.lparams(dip(32), dip(32)) {
                            topMargin = dip(48)
                            endToStart = R.id.anko_tv_date
                            topToTop = PARENT_ID
                        }

                        //添加按钮
                        textView("\uE6DC") {
                            id = R.id.anko_ib_add
                            backgroundResource = outValue.resourceId
                            textSize = 20f
                            gravity = Gravity.CENTER
                            includeFontPadding = false
                            setTypeface(iconFont, Typeface.BOLD)
                        }.lparams(dip(32), dip(32)) {
                            topMargin = dip(48)
                            endToStart = R.id.anko_ib_import
                            topToTop = PARENT_ID
                        }

                        //导入按钮
                        textView("\uE6E2") {
                            id = R.id.anko_ib_import
                            backgroundResource = outValue.resourceId
                            textSize = 20f
                            gravity = Gravity.CENTER
                            includeFontPadding = false
                            setTypeface(iconFont, Typeface.BOLD)
                        }.lparams(dip(32), dip(32)) {
                            topMargin = dip(48)
                            endToStart = R.id.anko_ib_more
                            topToTop = PARENT_ID
                        }

                        textView("\uE6BF") {
                            id = R.id.anko_ib_more
                            backgroundResource = outValue.resourceId
                            textSize = 20f
                            gravity = Gravity.CENTER
                            includeFontPadding = false
                            setTypeface(iconFont, Typeface.BOLD)
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
                        fitsSystemWindows = false
                        inflateHeaderView(R.layout.nav_header)
                        inflateMenu(R.menu.main_navigation_menu)
                    }.lparams(matchParent, matchParent) {
                        gravity = Gravity.START
                    }

                    navigationView {
                        id = R.id.anko_nv_end
                        fitsSystemWindows = false
                        recyclerView {
                            id = R.id.anko_rv_table_name
                            isNestedScrollingEnabled = false
                        }
                    }.lparams(dip(96), matchParent) {
                        gravity = Gravity.END
                    }
                }
            }.lparams(0, 0) {
                topToTop = PARENT_ID
                bottomToBottom = PARENT_ID
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
            }

            if (PreferenceUtils.getBooleanFromSP(context, "blur_main_nav_bar", false) && PreferenceUtils.getBooleanFromSP(context, "hide_main_nav_bar", false) && Build.VERSION.SDK_INT >= 21) {
                val barHeight = if (ViewUtils.getVirtualBarHeigh(context) != 0) {
                    ViewUtils.getVirtualBarHeigh(context)
                } else {
                    dip(48)
                }
                blurLayout {
                    id = R.id.anko_navigation_bar_blur_layout
                    setBlurRadius(50f)
                }.lparams(matchParent, barHeight) {
                    bottomToBottom = PARENT_ID
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                }
            }
        }
    }.view
}