package com.suda.yzune.wakeupschedule.base_view

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.inflate
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import com.suda.yzune.wakeupschedule.R
import splitties.dimensions.dip
import splitties.resources.styledColor

abstract class BaseBlurTitleActivity : BaseActivity() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    abstract fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView?

    lateinit var mainTitle: AppCompatTextView
    lateinit var llContent: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())
    }

    private fun createView(): View {
        val outValue = TypedValue()
        theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

        llContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, getStatusBarHeight() + dip(48), 0, 0)
            inflate(this@BaseBlurTitleActivity, layoutId, this)
        }

        mainTitle = AppCompatTextView(this).apply {
            text = title
            gravity = Gravity.CENTER_VERTICAL
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }

        return ConstraintLayout(this).apply {
            setBackgroundColor(styledColor(R.attr.colorSurface))
            addView(ScrollView(context).apply {
                overScrollMode = View.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                addView(llContent)
            }, ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            })

            addView(LinearLayout(context).apply {
                id = R.id.anko_layout
                setPadding(0, getStatusBarHeight(), 0, 0)
                setBackgroundColor(styledColor(R.attr.colorSurface))
                addView(ImageButton(context).apply {
                    setImageResource(R.drawable.ic_back)
                    setBackgroundResource(outValue.resourceId)
                    setPadding(dip(8))
                    setColorFilter(styledColor(R.attr.colorOnBackground))
                    setOnClickListener {
                        onBackPressed()
                    }
                }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dip(48)))

                addView(mainTitle, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dip(48))
                        .apply {
                            weight = 1f
                        })

                onSetupSubButton(AppCompatTextView(context).apply {
                    gravity = Gravity.CENTER_VERTICAL
                    setBackgroundResource(outValue.resourceId)
                    setPadding(dip(24), 0, dip(24), 0)
                })?.let {
                    addView(it, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dip(48)))
                }

            }, ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                topToTop = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            })
        }
    }

}