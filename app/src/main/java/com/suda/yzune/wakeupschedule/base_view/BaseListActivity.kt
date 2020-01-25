package com.suda.yzune.wakeupschedule.base_view

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.suda.yzune.wakeupschedule.R
import splitties.dimensions.dip
import splitties.resources.styledColor
import splitties.systemservices.inputMethodManager
import splitties.views.*
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.core.*
import splitties.views.dsl.recyclerview.recyclerView

abstract class BaseListActivity : BaseActivity() {

    abstract fun onSetupSubButton(tvButton: TextView): TextView?
    lateinit var mainTitle: TextView
    lateinit var searchView: EditText
    protected var showSearch = false
    protected var textWatcher: TextWatcher? = null
    protected lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())
    }

    private fun createView() = constraintLayout {

        val outValue = TypedValue()
        context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

        mRecyclerView = recyclerView {
            overScrollMode = OVER_SCROLL_NEVER
        }

        mainTitle = textView {
            text = title
            gravity = Gravity.CENTER_VERTICAL
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }

        searchView = editText {
            hint = "请输入……"
            textSize = 16f
            background = null
            gravity = Gravity.CENTER_VERTICAL
            visibility = View.GONE
            lines = 1
            setSingleLine()
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            addTextChangedListener(textWatcher)
        }

        add(mRecyclerView, lParams {
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
                setOnClickListener {
                    onBackPressed()
                }
            }, lParams(wrapContent, dip(48)))

            add(mainTitle, lParams(wrapContent, dip(48)) {
                weight = 1f
            })

            add(searchView, lParams(wrapContent, dip(48)) {
                weight = 1f
            })

            if (showSearch) {
                val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
                add(textView {
                    textSize = 20f
                    typeface = iconFont
                    text = "\uE6D4"
                    gravity = Gravity.CENTER
                    setBackgroundResource(outValue.resourceId)
                    setOnClickListener {
                        when (searchView.visibility) {
                            View.GONE -> {
                                mainTitle.visibility = View.GONE
                                searchView.visibility = View.VISIBLE
                                textColorResource = R.color.colorAccent
                                searchView.isFocusable = true
                                searchView.isFocusableInTouchMode = true
                                searchView.requestFocus()
                                inputMethodManager.showSoftInput(searchView, 0)
                            }
                        }
                    }
                }, lParams(wrapContent, dip(48)) {
                    marginEnd = dip(24)
                })
            }

            onSetupSubButton(textView {
                gravity = Gravity.CENTER
                setBackgroundResource(outValue.resourceId)
            })?.let {
                add(it, lParams(wrapContent, dip(48)) {
                    marginEnd = dip(24)
                })
            }

        }, lParams(matchParent, wrapContent) {
            topToTop = ConstraintSet.PARENT_ID
            startToStart = ConstraintSet.PARENT_ID
            endToEnd = ConstraintSet.PARENT_ID
        })
    }

    override fun onDestroy() {
        searchView.removeTextChangedListener(textWatcher)
        textWatcher = null
        super.onDestroy()
    }
}
