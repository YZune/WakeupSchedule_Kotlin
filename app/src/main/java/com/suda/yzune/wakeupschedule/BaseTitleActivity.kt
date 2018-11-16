package com.suda.yzune.wakeupschedule

import android.graphics.Typeface
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import org.jetbrains.anko.*

abstract class BaseTitleActivity : BaseActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        find<LinearLayout>(R.id.ll_root).addView(createTitleBar(), 0)
    }

    private fun createTitleBar(): View {
        return UI {
            verticalLayout {
                linearLayout {
                    topPadding = getStatusBarHeight()
                    backgroundColorResource = R.color.white
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

                    imageButton(R.drawable.ic_back) {
                        backgroundResource = outValue.resourceId
                        padding = dip(8)
                        setOnClickListener {
                            finish()
                        }
                    }.lparams(wrapContent, dip(48))

                    textView(this@BaseTitleActivity.title) {
                        gravity = Gravity.CENTER_VERTICAL
                        textSize = 16f
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams(wrapContent, dip(48)) {
                        weight = 1f
                    }

                    textView("保存") {
                        gravity = Gravity.CENTER_VERTICAL
                        horizontalPadding = dip(24)
                    }.lparams(wrapContent, dip(48))

                }

                view {
                    backgroundColorResource = R.color.grey
                    alpha = 0.5f
                }.lparams(wrapContent, dip(1))
            }
        }.view
    }

}