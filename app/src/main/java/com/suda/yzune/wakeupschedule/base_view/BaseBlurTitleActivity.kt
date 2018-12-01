package com.suda.yzune.wakeupschedule.base_view

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mmin18.widget.RealtimeBlurView
import com.suda.yzune.wakeupschedule.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.custom.ankoView

abstract class BaseBlurTitleActivity : BaseActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    abstract fun onSetupSubButton(tvButton: TextView): TextView?

    lateinit var mainTitle: TextView
    lateinit var llContent: LinearLayout

    private inline fun ViewManager.blurLayout(init: com.github.mmin18.widget.RealtimeBlurView.() -> Unit) = ankoView({ RealtimeBlurView(it, null) }, theme = 0) { init() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())
        LayoutInflater.from(this).inflate(layoutId, llContent, true)
    }

    private fun createView(): View {
        return UI {
            constraintLayout {

                scrollView {
                    overScrollMode = View.OVER_SCROLL_NEVER
                    isVerticalScrollBarEnabled = false
                    llContent = verticalLayout {
                        topPadding = getStatusBarHeight() + dip(48)
                    }.lparams(matchParent, matchParent)

                }.lparams(matchParent, matchParent) {
                    topToTop = ConstraintSet.PARENT_ID
                    bottomToTop = ConstraintSet.PARENT_ID
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                }

                if (Build.VERSION.SDK_INT >= 21) {
                    blurLayout {
                        id = R.id.anko_layout
                        setBlurRadius(50f)
                    }.lparams(matchParent, getStatusBarHeight() + dip(48)) {
                        topToTop = ConstraintSet.PARENT_ID
                        startToStart = ConstraintSet.PARENT_ID
                        endToEnd = ConstraintSet.PARENT_ID
                    }
                }

                linearLayout {
                    topPadding = getStatusBarHeight()
                    backgroundColorResource = if (Build.VERSION.SDK_INT >= 21) {
                        R.color.transparent
                    } else {
                        R.color.white
                    }
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

                    imageButton(R.drawable.ic_back) {
                        backgroundResource = outValue.resourceId
                        padding = dip(8)
                        setOnClickListener {
                            onBackPressed()
                        }
                    }.lparams(wrapContent, dip(48))

                    mainTitle = textView(this@BaseBlurTitleActivity.title) {
                        gravity = Gravity.CENTER_VERTICAL
                        textSize = 16f
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams(wrapContent, dip(48)) {
                        weight = 1f
                    }

                    onSetupSubButton(
                            textView {
                                gravity = Gravity.CENTER_VERTICAL
                                backgroundResource = outValue.resourceId
                                horizontalPadding = dip(24)
                            }.lparams(wrapContent, dip(48))
                    )
                }.lparams(matchParent, wrapContent) {
                    topToTop = ConstraintSet.PARENT_ID
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                }

                view {
                    backgroundColorResource = R.color.grey
                    alpha = 0.2f
                }.lparams(matchParent, dip(1)) {
                    topToBottom = R.id.anko_layout
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                }
            }
        }.view

    }
}