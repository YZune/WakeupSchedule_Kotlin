package com.suda.yzune.wakeupschedule.base_view

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.suda.yzune.wakeupschedule.R
import splitties.dimensions.dip
import splitties.resources.styledColor
import splitties.views.*
import splitties.views.dsl.core.*

abstract class BaseTitleActivity : BaseActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    open fun onSetupSubButton(tvButton: TextView): TextView? {
        return null
    }

    lateinit var mainTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        findViewById<LinearLayout>(R.id.ll_root).addView(createTitleBar(), 0)
    }

    open fun createTitleBar() = horizontalLayout {
        backgroundColor = styledColor(R.attr.colorSurface)
        topPadding = getStatusBarHeight()
        // backgroundColor = styledColor(R.attr.colorSurface)
        val outValue = TypedValue()
        context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

        add(imageButton {
            imageResource = R.drawable.ic_back
            setBackgroundResource(outValue.resourceId)
            padding = dip(8)
            setColorFilter(styledColor(R.attr.colorOnBackground))
            onClick {
                onBackPressed()
            }
        }, lParams(wrapContent, dip(48)))

        mainTitle = textView {
            text = title
            gravity = Gravity.CENTER_VERTICAL
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }

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

    }


}