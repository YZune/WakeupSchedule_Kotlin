package com.suda.yzune.wakeupschedule.schedule

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.core.content.res.ResourcesCompat
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.viewPager

class ScheduleActivityUI : AnkoComponent<ScheduleActivity> {

    override fun createView(ui: AnkoContext<ScheduleActivity>) = ui.apply {

        constraintLayout {

            val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
            val statusBarMargin = owner.getStatusBarHeight() + dip(8)

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
                            gravity = Gravity.CENTER
                            textColor = Color.BLACK
                            textSize = 20f
                            typeface = Typeface.DEFAULT_BOLD
                        }.lparams {
                            startToStart = PARENT_ID
                            topToTop = PARENT_ID
                            bottomToBottom = R.id.anko_ib_nav
                            marginStart = dip(24)
                            topMargin = statusBarMargin
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
                            typeface = iconFont
                        }.lparams(dip(32), dip(32)) {
                            topMargin = statusBarMargin
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
                            typeface = iconFont
                        }.lparams(dip(32), dip(32)) {
                            topMargin = statusBarMargin
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
                            typeface = iconFont
                        }.lparams(dip(32), dip(32)) {
                            topMargin = statusBarMargin
                            endToStart = R.id.anko_ib_share
                            topToTop = PARENT_ID
                        }

                        //分享按钮
                        textView("\uE6BA") {
                            id = R.id.anko_ib_share
                            backgroundResource = outValue.resourceId
                            textSize = 20f
                            gravity = Gravity.CENTER
                            includeFontPadding = false
                            typeface = iconFont
                        }.lparams(dip(32), dip(32)) {
                            topMargin = statusBarMargin
                            endToStart = R.id.anko_ib_more
                            topToTop = PARENT_ID
                        }

                        textView("\uE6BF") {
                            id = R.id.anko_ib_more
                            backgroundResource = outValue.resourceId
                            textSize = 20f
                            gravity = Gravity.CENTER
                            includeFontPadding = false
                            typeface = iconFont
                        }.lparams(dip(32), dip(32)) {
                            topMargin = statusBarMargin
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
                    }

                    navigationView {
                        id = R.id.anko_nv
                        backgroundColor = colorAttr(R.attr.colorSurface)
                        fitsSystemWindows = false
                        inflateHeaderView(R.layout.nav_header)
                        inflateMenu(R.menu.main_navigation_menu)
                        itemIconTintList = ViewUtils.createColorStateList(colorAttr(R.attr.colorOnBackground))
                    }.lparams(matchParent, matchParent) {
                        gravity = Gravity.START
                    }

                    navigationView {
                        id = R.id.anko_nv_end
                        backgroundColor = colorAttr(R.attr.colorSurface)
                        fitsSystemWindows = false
                        recyclerView {
                            id = R.id.anko_rv_table_name
                            overScrollMode = View.OVER_SCROLL_NEVER
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
        }
    }.view
}