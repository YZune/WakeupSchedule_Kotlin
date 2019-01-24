package com.suda.yzune.wakeupschedule.base_view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mmin18.widget.RealtimeBlurView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.recyclerview.v7.recyclerView

abstract class BaseListActivity : BaseActivity() {

    abstract fun onSetupSubButton(tvButton: TextView): TextView?
    lateinit var mainTitle: TextView
    lateinit var searchView: EditText
    protected var showSearch = false
    protected var textWatcher: TextWatcher? = null
    //protected val mAdapter: MultiTypeAdapter = MultiTypeAdapter()
    protected lateinit var mRecyclerView: RecyclerView

    private inline fun ViewManager.blurLayout(init: com.github.mmin18.widget.RealtimeBlurView.() -> Unit) = ankoView({ RealtimeBlurView(it, null) }, theme = 0) { init() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())
    }

    open fun onSetupSearchButton(visibility: Int, view: View) {}

    private fun createView(): View {
        return UI {
            constraintLayout {
                backgroundColor = Color.WHITE
                mRecyclerView = recyclerView {
                    overScrollMode = OVER_SCROLL_NEVER
                }.lparams(matchParent, matchParent) {
                    topToTop = ConstraintSet.PARENT_ID
                    bottomToTop = ConstraintSet.PARENT_ID
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                }

                if (Build.VERSION.SDK_INT >= 23 && PreferenceUtils.getBooleanFromSP(applicationContext, "title_blur", true)) {
                    blurLayout {
                        setBlurRadius(50f)
                    }.lparams(matchParent, getStatusBarHeight() + dip(48)) {
                        topToTop = ConstraintSet.PARENT_ID
                        startToStart = ConstraintSet.PARENT_ID
                        endToEnd = ConstraintSet.PARENT_ID
                    }
                }

                linearLayout {
                    id = R.id.anko_layout
                    topPadding = getStatusBarHeight()
                    backgroundColorResource = if (Build.VERSION.SDK_INT >= 23 && PreferenceUtils.getBooleanFromSP(applicationContext, "title_blur", true)) {
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

                    mainTitle = textView(this@BaseListActivity.title) {
                        gravity = Gravity.CENTER_VERTICAL
                        textSize = 16f
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams(wrapContent, dip(48)) {
                        weight = 1f
                    }

                    searchView = editText {
                        hint = "请输入学校……"
                        textSize = 16f
                        backgroundDrawable = null
                        gravity = Gravity.CENTER_VERTICAL
                        visibility = GONE
                        lines = 1
                        singleLine = true
                        imeOptions = EditorInfo.IME_ACTION_SEARCH
                        addTextChangedListener(textWatcher)
                    }.lparams(wrapContent, dip(48)) {
                        weight = 1f
                    }

                    if (showSearch) {
                        val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
                        textView {
                            textSize = 20f
                            typeface = iconFont
                            text = "\uE6D4"
                            gravity = Gravity.CENTER_VERTICAL
                            backgroundResource = outValue.resourceId
                            setOnClickListener {
                                onSetupSearchButton(searchView.visibility, it)
                            }
                        }.lparams(wrapContent, dip(48))
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
