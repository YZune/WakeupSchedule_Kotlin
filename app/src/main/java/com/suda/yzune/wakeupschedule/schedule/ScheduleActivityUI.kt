package com.suda.yzune.wakeupschedule.schedule

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.Ui
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils.getStatusBarHeight
import splitties.dimensions.dip
import splitties.resources.styledColor

class ScheduleActivityUI(override val ctx: Context) : Ui {

    override val root = ConstraintLayout(ctx).apply {

        val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
        val statusBarMargin = getStatusBarHeight(ctx) + dip(8)
        val outValue = TypedValue()
        context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

        val drawerLayout = DrawerLayout(ctx).apply {
            id = R.id.anko_drawer_layout
            addView(ConstraintLayout(ctx).apply {
                id = R.id.anko_cl_schedule
                addView(AppCompatImageView(ctx).apply {
                    id = R.id.anko_iv_bg
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }, ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
                    bottomToBottom = PARENT_ID
                })

                addView(AppCompatTextView(ctx).apply {
                    id = R.id.anko_tv_date
                    gravity = Gravity.CENTER
                    setTextColor(Color.BLACK)
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                }, ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    startToStart = PARENT_ID
                    topToTop = PARENT_ID
                    bottomToBottom = R.id.anko_ib_nav
                    marginStart = dip(24)
                    topMargin = statusBarMargin
                })

                addView(AppCompatTextView(ctx).apply {
                    id = R.id.anko_tv_week
                    setTextColor(Color.BLACK)
                }, ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    startToStart = R.id.anko_tv_date
                    topToBottom = R.id.anko_tv_date
                    topMargin = dip(4)
                })

                addView(AppCompatTextView(ctx).apply {
                    id = R.id.anko_tv_weekday
                    setTextColor(Color.BLACK)
                }, ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    startToEnd = R.id.anko_tv_week
                    topToBottom = R.id.anko_tv_date
                    topMargin = dip(4)
                    marginStart = dip(8)
                })

                //导航按钮
                addView(AppCompatTextView(ctx).apply {
                    id = R.id.anko_ib_nav
                    text = "\uE6A7"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_tv_date
                    topToTop = PARENT_ID
                })

                //添加按钮
                addView(AppCompatTextView(ctx).apply {
                    id = R.id.anko_ib_add
                    text = "\uE6DC"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_import
                    topToTop = PARENT_ID
                })

                //导入按钮
                addView(AppCompatTextView(ctx).apply {
                    id = R.id.anko_ib_import
                    text = "\uE6E2"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_share
                    topToTop = PARENT_ID
                })

                //分享按钮
                addView(AppCompatTextView(ctx).apply {
                    id = R.id.anko_ib_share
                    text = "\uE6BA"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_more
                    topToTop = PARENT_ID
                })

                addView(AppCompatTextView(ctx).apply {
                    id = R.id.anko_ib_more
                    text = "\uE6BF"
                    setBackgroundResource(outValue.resourceId)
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
                    topMargin = statusBarMargin
                    marginEnd = dip(8)
                    endToEnd = PARENT_ID
                    topToTop = PARENT_ID
                })

                val viewPager: ViewPager = ViewPager(ctx).apply {
                    id = R.id.anko_vp_schedule
                }

                addView(viewPager, ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
                    topToBottom = R.id.anko_tv_week
                    bottomToBottom = PARENT_ID
                    startToStart = PARENT_ID
                    endToEnd = PARENT_ID
                })

            }, DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT,
                    DrawerLayout.LayoutParams.MATCH_PARENT))

            addView(NavigationView(ctx).apply {
                id = R.id.anko_nv
                setBackgroundColor(styledColor(R.attr.colorSurface))
                fitsSystemWindows = false
                inflateHeaderView(R.layout.nav_header)
                inflateMenu(R.menu.main_navigation_menu)
                itemIconTintList = ViewUtils.createColorStateList(styledColor(R.attr.colorOnBackground))
            }, DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT,
                    DrawerLayout.LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.START
            })

            addView(NavigationView(ctx).apply {
                id = R.id.anko_nv_end
                setBackgroundColor(styledColor(R.attr.colorSurface))
                fitsSystemWindows = false
                addView(RecyclerView(ctx).apply {
                    id = R.id.anko_rv_table_name
                    overScrollMode = View.OVER_SCROLL_NEVER
                }, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT))
            }, DrawerLayout.LayoutParams(dip(96), DrawerLayout.LayoutParams.MATCH_PARENT)
                    .apply {
                        gravity = Gravity.END
                    })
        }

        addView(drawerLayout, ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
            topToTop = PARENT_ID
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
            bottomToBottom = PARENT_ID
        })

    }

}