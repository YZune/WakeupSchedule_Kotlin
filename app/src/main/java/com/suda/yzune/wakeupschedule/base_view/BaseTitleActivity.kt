package com.suda.yzune.wakeupschedule.base_view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.suda.yzune.wakeupschedule.R
import org.jetbrains.anko.*

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
        find<LinearLayout>(R.id.ll_root).addView(createTitleBar(), 0)
    }

    open fun createTitleBar(): View {
        return UI {
            verticalLayout {
                backgroundColor = colorAttr(R.attr.colorSurface)
                linearLayout {
                    topPadding = getStatusBarHeight()
                    backgroundColor = colorAttr(R.attr.colorSurface)
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

                    imageButton(R.drawable.ic_back) {
                        backgroundResource = outValue.resourceId
                        padding = dip(8)
                        setColorFilter(colorAttr(R.attr.colorOnBackground))
                        setOnClickListener {
                            onBackPressed()
                        }
                    }.lparams(wrapContent, dip(48))

                    mainTitle = textView(this@BaseTitleActivity.title) {
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

                }

//                view {
//                    backgroundColorResource = R.color.grey
//                    alpha = 0.5f
//                }.lparams(wrapContent, dip(1))
            }
        }.view
    }
}