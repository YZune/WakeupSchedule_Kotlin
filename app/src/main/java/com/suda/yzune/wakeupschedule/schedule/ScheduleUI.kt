package com.suda.yzune.wakeupschedule.schedule

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import splitties.dimensions.dip
import splitties.dimensions.dp
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.constraintlayout.parentId
import splitties.views.dsl.core.*

class ScheduleUI(override val ctx: Context, table: TableBean, day: Int) : Ui {

    private var col = 6

    val dayMap = IntArray(8)

    init {
        for (i in 1..7) {
            if (!table.sundayFirst || !table.showSun) {
                if (!table.showSat && i == 7) {
                    dayMap[i] = 6
                } else {
                    dayMap[i] = i
                }
            } else {
                if (i == 7) {
                    dayMap[i] = 1
                } else {
                    dayMap[i] = i + 1
                }
            }
        }
        if (table.showSat) {
            col++
        } else {
            dayMap[6] = -1
        }
        if (table.showSun) {
            col++
        } else {
            dayMap[7] = -1
        }
    }

    val content = constraintLayout(R.id.anko_cl_content_panel) {
        for (i in 1..table.nodes) {
            add(textView(R.id.anko_tv_node1 + i - 1) {
                text = i.toString()
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(table.textColor)
            }, lParams(0, dip(table.itemHeight)) {
                topMargin = dip(2)
                endToStart = R.id.anko_ll_week_panel_0
                horizontalWeight = 0.5f
                startToStart = ConstraintSet.PARENT_ID
                when (i) {
                    1 -> {
                        bottomToTop = R.id.anko_tv_node1 + i
                        topToTop = ConstraintSet.PARENT_ID
                        verticalBias = 0f
                        verticalChainStyle = ConstraintSet.CHAIN_PACKED
                    }
                    table.nodes -> {
                        bottomToTop = R.id.anko_navigation_bar_view
                        topToBottom = R.id.anko_tv_node1 + i - 2
                    }
                    else -> {
                        bottomToTop = R.id.anko_tv_node1 + i
                        topToBottom = R.id.anko_tv_node1 + i - 2
                    }
                }
            })
        }

        val barHeight = if (ViewUtils.getVirtualBarHeight(context) in 1..48) {
            ViewUtils.getVirtualBarHeight(context)
        } else {
            dip(48)
        }

        val navBar = view(::View, R.id.anko_navigation_bar_view)

        if (PreferenceUtils.getBooleanFromSP(context, "hide_main_nav_bar", false) && Build.VERSION.SDK_INT >= 19) {
            add(navBar, lParams(matchParent, barHeight) {
                topToBottom = R.id.anko_tv_node1 + table.nodes - 1
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            })
        }

        for (i in 0 until col - 1) {
            add(frameLayout(R.id.anko_ll_week_panel_0 + i), lParams(0, wrapContent) {
                marginStart = dip(1)
                marginEnd = dip(1)
                horizontalWeight = 1f
                when (i) {
                    0 -> {
                        startToEnd = R.id.anko_tv_node1
                        endToStart = R.id.anko_ll_week_panel_0 + i + 1
                    }
                    col - 2 -> {
                        startToEnd = R.id.anko_ll_week_panel_0 + i - 1
                        endToEnd = ConstraintSet.PARENT_ID
                    }
                    else -> {
                        startToEnd = R.id.anko_ll_week_panel_0 + i - 1
                        endToStart = R.id.anko_ll_week_panel_0 + i + 1
                    }
                }
            })
        }
    }

    val scrollView = content.wrapInScrollView(R.id.anko_sv_schedule) {
        overScrollMode = View.OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
    }

    override val root = constraintLayout {
        for (i in 0 until col) {
            add(textView(R.id.anko_tv_title0 + i) {
                setPadding(0, dip(8), 0, dip(8))
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(table.textColor)
                setLineSpacing(dp(2), 1f)
                if (i == 0 || i == dayMap[day]) {
                    typeface = Typeface.DEFAULT_BOLD
                    alpha = 1f
                } else {
                    alpha = 0.32f
                }
            }, lParams(0, wrapContent) {
                when (i) {
                    0 -> {
                        horizontalWeight = 0.5f
                        startToStart = ConstraintSet.PARENT_ID
                        topToTop = ConstraintSet.PARENT_ID
                        endToStart = R.id.anko_tv_title0 + i + 1
                    }
                    col - 1 -> {
                        horizontalWeight = 1f
                        startToEnd = R.id.anko_tv_title0 + i - 1
                        endToEnd = ConstraintSet.PARENT_ID
                        baselineToBaseline = R.id.anko_tv_title0 + i - 1
                    }
                    else -> {
                        horizontalWeight = 1f
                        startToEnd = R.id.anko_tv_title0 + i - 1
                        endToStart = R.id.anko_tv_title0 + i + 1
                        baselineToBaseline = R.id.anko_tv_title0 + i - 1
                    }
                }
            })
        }

        add(scrollView, lParams {
            bottomToBottom = ConstraintSet.PARENT_ID
            topToBottom = R.id.anko_tv_title0
            startToStart = parentId
            endToEnd = parentId
        })
    }
}
