package com.suda.yzune.wakeupschedule.schedule

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils.getStatusBarHeight
import splitties.dimensions.dip
import splitties.resources.styledColor
import splitties.views.backgroundColor
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.core.*
import splitties.views.dsl.material.navigationView
import splitties.views.dsl.recyclerview.recyclerView

class ScheduleActivityUI(override val ctx: Context) : Ui {

    override val root = constraintLayout {

        val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
        val statusBarMargin = getStatusBarHeight(ctx) + dip(8)
        val outValue = TypedValue()
        context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

        val drawerLayout: DrawerLayout = view(::DrawerLayout, R.id.anko_drawer_layout) {

            add(constraintLayout(R.id.anko_cl_schedule) {

                add(imageView(R.id.anko_iv_bg) {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }, lParams {
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID
                })

                add(textView(R.id.anko_tv_date) {
                    gravity = Gravity.CENTER
                    setTextColor(Color.BLACK)
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                }, lParams(wrapContent, wrapContent) {
                    startToStart = PARENT_ID
                    topToTop = PARENT_ID
                    bottomToBottom = R.id.anko_ib_nav
                    marginStart = dip(24)
                    topMargin = statusBarMargin
                })

                add(textView(R.id.anko_tv_week) {
                    setTextColor(Color.BLACK)
                }, lParams(wrapContent, wrapContent) {
                    startToStart = R.id.anko_tv_date
                    topToBottom = R.id.anko_tv_date
                    topMargin = dip(4)
                })

                add(textView(R.id.anko_tv_weekday) {
                    setTextColor(Color.BLACK)
                }, lParams(wrapContent, wrapContent) {
                    startToEnd = R.id.anko_tv_week
                    topToBottom = R.id.anko_tv_date
                    topMargin = dip(4)
                    marginStart = dip(8)
                })

                //导航按钮
                add(textView(R.id.anko_ib_nav) {
                    text = "\uE6A7"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, lParams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_tv_date
                    topToTop = PARENT_ID
                })

                //添加按钮
                add(textView(R.id.anko_ib_add) {
                    text = "\uE6DC"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, lParams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_import
                    topToTop = PARENT_ID
                })

                //导入按钮
                add(textView(R.id.anko_ib_import) {
                    text = "\uE6E2"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, lParams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_share
                    topToTop = PARENT_ID
                })

                //分享按钮
                add(textView(R.id.anko_ib_share) {
                    text = "\uE6BA"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, lParams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_more
                    topToTop = PARENT_ID
                })

                add(textView(R.id.anko_ib_more) {
                    text = "\uE6BF"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, lParams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    marginEnd = dip(8)
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
                })

                val viewPager: ViewPager = view(::ViewPager, R.id.anko_vp_schedule)
                add(viewPager, lParams {
                    topToBottom = R.id.anko_tv_week
                    bottomToBottom = PARENT_ID
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                })

            }, DrawerLayout.LayoutParams(matchParent, matchParent))

            add(navigationView(id = R.id.anko_nv) {
                backgroundColor = styledColor(R.attr.colorSurface)
                fitsSystemWindows = false
                inflateHeaderView(R.layout.nav_header)
                inflateMenu(R.menu.main_navigation_menu)
                itemIconTintList = ViewUtils.createColorStateList(styledColor(R.attr.colorOnBackground))
            }, DrawerLayout.LayoutParams(matchParent, matchParent).apply {
                gravity = Gravity.START
            })

            add(navigationView {
                id = R.id.anko_nv_end
                backgroundColor = styledColor(R.attr.colorSurface)
                fitsSystemWindows = false
                add(recyclerView {
                    id = R.id.anko_rv_table_name
                    overScrollMode = View.OVER_SCROLL_NEVER
                }, lParams())
            }, DrawerLayout.LayoutParams(dip(96), matchParent).apply {
                gravity = Gravity.END
            })
        }

        add(drawerLayout, lParams {
            topToTop = PARENT_ID
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
            bottomToBottom = PARENT_ID
        })

    }

}