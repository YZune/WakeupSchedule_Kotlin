package com.suda.yzune.wakeupschedule.schedule

import android.animation.StateListAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setMargins
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.Ui
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils.getStatusBarHeight
import com.suda.yzune.wakeupschedule.utils.getPrefer
import splitties.dimensions.dip
import splitties.dimensions.dp
import splitties.resources.colorSL
import splitties.resources.styledColor

class ScheduleActivityUI(override val ctx: Context) : Ui {

    private val iconFont = ResourcesCompat.getFont(ctx, R.font.iconfont)
    private val statusBarMargin = getStatusBarHeight(ctx) + ctx.dip(8)
    private val outValue = TypedValue()

    val viewPager: ViewPager = ViewPager(ctx).apply {
        id = R.id.anko_vp_schedule
    }

    val bg = AppCompatImageView(ctx).apply {
        id = R.id.anko_iv_bg
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    val dateView = AppCompatTextView(ctx).apply {
        id = R.id.anko_tv_date
        gravity = Gravity.CENTER
        setTextColor(Color.BLACK)
        textSize = 20f
        typeface = Typeface.DEFAULT_BOLD
    }

    val weekView = AppCompatTextView(ctx).apply {
        id = R.id.anko_tv_week
        setTextColor(Color.BLACK)
    }

    val weekDayView = AppCompatTextView(ctx).apply {
        id = R.id.anko_tv_weekday
        setTextColor(Color.BLACK)
    }

    val navBtn = AppCompatTextView(ctx).apply {
        id = R.id.anko_ib_nav
        text = "\uE6A7"
        setBackgroundResource(outValue.resourceId)
        textSize = 20f
        gravity = Gravity.CENTER
        includeFontPadding = false
        typeface = iconFont
    }

    val addBtn = AppCompatTextView(ctx).apply {
        id = R.id.anko_ib_add
        text = "\uE6DC"
        setBackgroundResource(outValue.resourceId)
        textSize = 20f
        gravity = Gravity.CENTER
        includeFontPadding = false
        typeface = iconFont
    }

    val importBtn = AppCompatTextView(ctx).apply {
        id = R.id.anko_ib_import
        text = "\uE6E2"
        setBackgroundResource(outValue.resourceId)
        textSize = 20f
        gravity = Gravity.CENTER
        includeFontPadding = false
        typeface = iconFont
    }

    val shareBtn = AppCompatTextView(ctx).apply {
        id = R.id.anko_ib_share
        text = "\uE6BA"
        setBackgroundResource(outValue.resourceId)
        textSize = 20f
        gravity = Gravity.CENTER
        includeFontPadding = false
        typeface = iconFont
    }

    val moreBtn = AppCompatTextView(ctx).apply {
        id = R.id.anko_ib_more
        text = "\uE6BF"
        setBackgroundResource(outValue.resourceId)
        textSize = 20f
        gravity = Gravity.CENTER
        includeFontPadding = false
        typeface = iconFont
    }

    val content = ConstraintLayout(ctx).apply {
        id = R.id.anko_cl_schedule
        context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)
        addView(bg, ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
            topToTop = PARENT_ID
            bottomToBottom = PARENT_ID
        })

        addView(dateView, ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToStart = PARENT_ID
            topToTop = PARENT_ID
            bottomToBottom = R.id.anko_ib_nav
            marginStart = dip(24)
            topMargin = statusBarMargin
        })

        addView(weekView, ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToStart = R.id.anko_tv_date
            topToBottom = R.id.anko_tv_date
            topMargin = dip(4)
        })

        addView(weekDayView, ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToEnd = R.id.anko_tv_week
            topToBottom = R.id.anko_tv_date
            topMargin = dip(4)
            marginStart = dip(8)
        })

        //导航按钮
        addView(navBtn, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
            topMargin = statusBarMargin
            endToStart = R.id.anko_tv_date
            topToTop = PARENT_ID
        })

        //添加按钮
        addView(addBtn, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
            topMargin = statusBarMargin
            endToStart = R.id.anko_ib_import
            topToTop = PARENT_ID
        })

        //导入按钮
        addView(importBtn, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
            topMargin = statusBarMargin
            endToStart = R.id.anko_ib_share
            topToTop = PARENT_ID
        })

        //分享按钮
        addView(shareBtn, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
            topMargin = statusBarMargin
            endToStart = R.id.anko_ib_more
            topToTop = PARENT_ID
        })

        addView(moreBtn, ConstraintLayout.LayoutParams(dip(32), dip(32)).apply {
            topMargin = statusBarMargin
            marginEnd = dip(8)
            endToEnd = PARENT_ID
            topToTop = PARENT_ID
        })

        addView(viewPager, ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
            topToBottom = R.id.anko_tv_week
            bottomToBottom = PARENT_ID
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
        })

    }

    val navViewStart = NavigationView(ctx).apply {
        id = R.id.anko_nv
        setBackgroundColor(styledColor(R.attr.colorSurface))
        fitsSystemWindows = false
        inflateHeaderView(R.layout.nav_header)
        inflateMenu(R.menu.main_navigation_menu)
        itemIconTintList = ViewUtils.createColorStateList(styledColor(R.attr.colorOnBackground))
    }

    val rvTableName = RecyclerView(ctx).apply {
        id = R.id.bottom_sheet_rv_table
        overScrollMode = View.OVER_SCROLL_NEVER
        layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.HORIZONTAL
        }
    }

    val drawerLayout = DrawerLayout(ctx).apply {
        id = R.id.anko_drawer_layout
        addView(content, DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT,
                DrawerLayout.LayoutParams.MATCH_PARENT))

        addView(navViewStart, DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT,
                DrawerLayout.LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.START
        })
    }

    val changeWeekBtn = createTextButton().apply {
        id = R.id.bottom_sheet_change_week_btn
        text = "修改当前周"
        minWidth = 0
        minimumWidth = 0
        textSize = 12f
    }

    val createScheduleBtn = createTextButton().apply {
        id = R.id.bottom_sheet_create_schedule_btn
        text = "新建课表"
        minWidth = 0
        minimumWidth = 0
        textSize = 12f
    }

    val manageScheduleBtn = createTextButton().apply {
        id = R.id.bottom_sheet_manage_schedule_btn
        text = "管理"
        minWidth = 0
        minimumWidth = 0
        textSize = 12f
    }

    val weekToggleGroup = MaterialButtonToggleGroup(ctx).apply {
        id = R.id.bottom_sheet_cg_week
        isSingleSelection = true
        isSelectionRequired = true
    }

    val weekScrollView = HorizontalScrollView(ctx).apply {
        id = R.id.bottom_sheet_sv_week
        overScrollMode = View.OVER_SCROLL_NEVER
        isHorizontalScrollBarEnabled = false
        addView(weekToggleGroup)
    }

    val timeBtn = createTextButton().apply {
        id = R.id.bottom_sheet_modify_time_btn
        text = "上课时间"
        minWidth = 0
        minimumWidth = 0
        textSize = 12f
    }

    val changeBgBtn = createTextButton().apply {
        id = R.id.bottom_sheet_bg_btn
        text = "更换背景"
        minWidth = 0
        minimumWidth = 0
        textSize = 12f
    }

    val courseBtn = createTextButton().apply {
        id = R.id.bottom_sheet_check_course_btn
        text = "已添课程"
        minWidth = 0
        minimumWidth = 0
        textSize = 12f
    }

    val qaBtn = createTextButton().apply {
        id = R.id.bottom_sheet_question_btn
        text = "常见问题"
        minWidth = 0
        minimumWidth = 0
        textSize = 12f
    }

    val cardContent = ConstraintLayout(ctx).apply {
        val space = dip(16)
        setPadding(space, 0, space, 0)
        isMotionEventSplittingEnabled = false
        addView(AppCompatTextView(context).apply {
            id = R.id.bottom_sheet_title_week
            text = "周数"
            textSize = 12f
        }, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToStart = PARENT_ID
            topToTop = PARENT_ID
            topMargin = dip(16)
        })
        addView(changeWeekBtn, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            endToEnd = PARENT_ID
            topToTop = R.id.bottom_sheet_title_week
            bottomToBottom = R.id.bottom_sheet_title_week
        })
        addView(weekScrollView, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
            topToBottom = R.id.bottom_sheet_title_week
            topMargin = dip(8)
        })
        addView(AppCompatTextView(context).apply {
            id = R.id.bottom_sheet_title_schedule
            text = "多课表"
            textSize = 12f
        }, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToStart = PARENT_ID
            topToBottom = R.id.bottom_sheet_sv_week
            topMargin = dip(8)
        })
        addView(rvTableName, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
            topToBottom = R.id.bottom_sheet_title_schedule
            topMargin = dip(16)
        })
        addView(manageScheduleBtn, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            endToEnd = PARENT_ID
            topToTop = R.id.bottom_sheet_title_schedule
            bottomToBottom = R.id.bottom_sheet_title_schedule
        })
        addView(createScheduleBtn, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            endToStart = R.id.bottom_sheet_manage_schedule_btn
            topToTop = R.id.bottom_sheet_title_schedule
            bottomToBottom = R.id.bottom_sheet_title_schedule
        })
        addView(AppCompatTextView(context).apply {
            id = R.id.bottom_sheet_title_shortcut
            text = "捷径"
            textSize = 12f
        }, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToStart = PARENT_ID
            topToBottom = R.id.bottom_sheet_rv_table
            topMargin = dip(16)
        })
        addView(timeBtn, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToStart = PARENT_ID
            topToBottom = R.id.bottom_sheet_title_shortcut
            endToStart = R.id.bottom_sheet_bg_btn
        })
        addView(changeBgBtn, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToEnd = R.id.bottom_sheet_modify_time_btn
            topToBottom = R.id.bottom_sheet_title_shortcut
            endToStart = R.id.bottom_sheet_check_course_btn
        })
        addView(courseBtn, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToEnd = R.id.bottom_sheet_bg_btn
            topToBottom = R.id.bottom_sheet_title_shortcut
            endToStart = R.id.bottom_sheet_question_btn
        })
        addView(qaBtn, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
            startToEnd = R.id.bottom_sheet_check_course_btn
            topToBottom = R.id.bottom_sheet_title_shortcut
        })
    }

    val bottomSheet = FrameLayout(ctx).apply {
        addView(MaterialCardView(context).apply {
            setCardBackgroundColor(styledColor(R.attr.colorSurface))
            cardElevation = dp(8)
            addView(cardContent, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip(320)).apply {
            gravity = Gravity.BOTTOM
            setMargins(dip(16))
            if (context.getPrefer().getBoolean(Const.KEY_HIDE_NAV_BAR, false)) {
                bottomMargin = dip(16) + ViewUtils.getVirtualBarHeight(ctx)
            }
        })
    }

    override val root = CoordinatorLayout(ctx).apply {

        addView(drawerLayout, CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT)
        )

        addView(bottomSheet, CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                ViewUtils.getScreenInfo(context)[1]).apply {
            behavior = BottomSheetBehavior<FrameLayout>(ctx, null).apply {
                isHideable = true
                peekHeight = 0
            }
        })

    }

    fun createTextButton() = MaterialButton(ctx).apply {
        setTextColor(colorSL(R.color.mtrl_text_btn_text_color_selector))
        val space = dip(8)
        setPadding(space, 0, space, 0)
        backgroundTintList = colorSL(R.color.mtrl_btn_text_btn_bg_color_selector)
        rippleColor = colorSL(R.color.mtrl_btn_text_btn_ripple_color)
        elevation = 0f
        stateListAnimator = StateListAnimator()
    }

    fun createOutlineButton() = createTextButton().apply {
        strokeColor = colorSL(R.color.mtrl_btn_stroke_color_selector)
        strokeWidth = dip(1)
    }

}