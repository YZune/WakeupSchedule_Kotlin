package com.suda.yzune.wakeupschedule

import android.os.Bundle
import android.support.annotation.LayoutRes
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

                view {
                    backgroundColorResource = R.color.white
                }.lparams(matchParent, getStatusBarHeight())

                linearLayout {
                    imageButton(R.drawable.ic_back) {
                        padding = dip(8)
                    }.lparams(wrapContent, matchParent)

                }.lparams(matchParent, dip(48))
            }
        }.view
    }

}