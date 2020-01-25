package com.suda.yzune.wakeupschedule.base_view

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintSet
import com.suda.yzune.wakeupschedule.R
import splitties.dimensions.dip
import splitties.resources.styledColor
import splitties.views.*
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.core.*

abstract class BaseBlurTitleActivity : BaseActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    abstract fun onSetupSubButton(tvButton: TextView): TextView?

    lateinit var mainTitle: TextView
    lateinit var llContent: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())
    }

    private fun createView(): View {
        val outValue = TypedValue()
        theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

        llContent = verticalLayout {
            topPadding = getStatusBarHeight() + dip(48)
            add(inflate<View>(layoutId), lParams(matchParent, matchParent))
        }

        mainTitle = textView {
            text = title
            gravity = Gravity.CENTER_VERTICAL
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }

        return constraintLayout {
            backgroundColor = styledColor(R.attr.colorSurface)

            add(llContent.wrapInScrollView {
                overScrollMode = View.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
            }, lParams {
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            })

            add(horizontalLayout {
                id = R.id.anko_layout
                topPadding = getStatusBarHeight()
                backgroundColor = styledColor(R.attr.colorSurface)

                add(imageButton {
                    imageResource = R.drawable.ic_back
                    setBackgroundResource(outValue.resourceId)
                    padding = dip(8)
                    setColorFilter(styledColor(R.attr.colorOnBackground))
                    onClick {
                        onBackPressed()
                    }
                }, lParams(wrapContent, dip(48)))

                add(mainTitle, lParams(wrapContent, dip(48)) {
                    weight = 1f
                })

                onSetupSubButton(textView {
                    gravity = Gravity.CENTER_VERTICAL
                    setBackgroundResource(outValue.resourceId)
                    horizontalPadding = dip(24)
                })?.let {
                    add(it, lParams(wrapContent, dip(48)))
                }

            }, lParams(matchParent, wrapContent) {
                topToTop = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            })
        }
    }

}