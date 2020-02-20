package com.suda.yzune.wakeupschedule.schedule

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.ColorUtils
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.Ui
import com.suda.yzune.wakeupschedule.bean.TableBean
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.getPrefer
import splitties.dimensions.dip
import splitties.dimensions.dp

class ScheduleUI(override val ctx: Context, table: TableBean, day: Int, forWidget: Boolean = false) : Ui {

    private var col = 6

    var showTimeDetail = true

    val dayMap = IntArray(8)
    val itemHeight = ctx.dip(if (forWidget) table.widgetItemHeight else table.itemHeight)
    val textColor = if (forWidget) table.widgetTextColor else table.textColor

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

    val content = ConstraintLayout(ctx).apply {
        id = R.id.anko_cl_content_panel
        val timeSize = when (col) {
            7 -> 9f
            6 -> 10f
            else -> 8f
        }
        for (i in 1..table.nodes) {
            addView(FrameLayout(context).apply {
                id = R.id.anko_tv_node1 + i - 1
                if (showTimeDetail) {
                    addView(AppCompatTextView(context).apply {
                        id = R.id.tv_start
                        setTextColor(textColor)
                        //gravity = Gravity.CENTER
                        //textAlignment = View.TEXT_ALIGNMENT_CENTER
                        setSingleLine()
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, timeSize)
                    }, FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                    })
                    addView(AppCompatTextView(context).apply {
                        id = R.id.tv_end
                        setTextColor(textColor)
                        setSingleLine()
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, timeSize)
                    }, FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                    })
                }
                addView(AppCompatTextView(context).apply {
                    setTextColor(textColor)
                    text = i.toString()
                    textSize = 12f
                    setSingleLine()
                }, FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                })
            }, ConstraintLayout.LayoutParams(0, itemHeight).apply {
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
                        //bottomToTop = R.id.anko_navigation_bar_view
                        bottomToBottom = ConstraintSet.PARENT_ID
                        topToBottom = R.id.anko_tv_node1 + i - 2
                    }
                    else -> {
                        bottomToTop = R.id.anko_tv_node1 + i
                        topToBottom = R.id.anko_tv_node1 + i - 2
                    }
                }
            })
        }

        if (!forWidget && context.getPrefer().getBoolean(Const.KEY_SCHEDULE_BLANK_AREA, true)) {
            addView(View(context), ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, itemHeight * 4).apply {
                topToBottom = R.id.anko_tv_node1 + table.nodes - 1
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            })
        }

        for (i in 0 until col - 1) {
            addView(FrameLayout(context).apply { id = R.id.anko_ll_week_panel_0 + i }, ConstraintLayout.LayoutParams(0,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
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
                        if (!forWidget) {
                            marginEnd = if (col < 8) {
                                dip(8)
                            } else {
                                dip(4)
                            }
                        }
                    }
                    else -> {
                        startToEnd = R.id.anko_ll_week_panel_0 + i - 1
                        endToStart = R.id.anko_ll_week_panel_0 + i + 1
                    }
                }
            })
        }
    }

    val scrollView = ScrollView(ctx).apply {
        id = R.id.anko_sv_schedule
        overScrollMode = View.OVER_SCROLL_NEVER
        isVerticalScrollBarEnabled = false
        addView(content)
    }

    override val root = ConstraintLayout(ctx).apply {
        val textAlphaColor = ColorUtils.setAlphaComponent(textColor, (0.32 * (textColor shr 24 and 0xff)).toInt())
        for (i in 0 until col) {
            addView(AppCompatTextView(context).apply {
                id = R.id.anko_tv_title0 + i
                setPadding(0, dip(8), 0, dip(8))
                textSize = 12f
                gravity = Gravity.CENTER
                setLineSpacing(dp(2), 1f)
                if (i == 0 || (day > 0 && i == dayMap[day])) {
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(textColor)
                } else {
                    setTextColor(textAlphaColor)
                }
            }, ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
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
                        if (!forWidget) {
                            marginEnd = if (col < 8) {
                                dip(8)
                            } else {
                                dip(4)
                            }
                        }
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

        addView(scrollView, ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
            bottomToBottom = ConstraintSet.PARENT_ID
            topToBottom = R.id.anko_tv_title0
            startToStart = ConstraintSet.PARENT_ID
            endToEnd = ConstraintSet.PARENT_ID
        })
    }
}
