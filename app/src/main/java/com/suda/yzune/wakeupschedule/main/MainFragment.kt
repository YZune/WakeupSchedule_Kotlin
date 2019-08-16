package com.suda.yzune.wakeupschedule.main


import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat

import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseActivity
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.viewPager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val iconFont = ResourcesCompat.getFont(context!!, R.font.iconfont)
        val statusBarMargin = (activity as BaseActivity).getStatusBarHeight() + dip(8)
        val outValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

        return UI {
            constraintLayout {
                id = R.id.anko_cl_schedule

                imageView {
                    id = R.id.anko_iv_bg
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams(matchParent, matchParent) {
                    startToStart = ConstraintSet.PARENT_ID
                    endToEnd = ConstraintSet.PARENT_ID
                    topToTop = ConstraintSet.PARENT_ID
                    bottomToBottom = ConstraintSet.PARENT_ID
                }

                textView {
                    id = R.id.anko_tv_date
                    textColor = Color.BLACK
                    textSize = 24f
                    typeface = Typeface.DEFAULT_BOLD
                }.lparams {
                    startToStart = ConstraintSet.PARENT_ID
                    topToTop = ConstraintSet.PARENT_ID
                    marginStart = dip(24)
                    topMargin = statusBarMargin
                }

                textView {
                    id = R.id.anko_tv_week
                    textColor = Color.BLACK
                }.lparams {
                    startToStart = R.id.anko_tv_date
                    topToBottom = R.id.anko_tv_date
                    topMargin = dip(4)
                }

                textView {
                    id = R.id.anko_tv_weekday
                    textColor = Color.BLACK
                }.lparams {
                    startToEnd = R.id.anko_tv_week
                    topToBottom = R.id.anko_tv_date
                    topMargin = dip(4)
                    marginStart = dip(8)
                }

                // 导航按钮
                textView("\uE6A7") {
                    id = R.id.anko_ib_nav
                    backgroundResource = outValue.resourceId
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }.lparams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_tv_date
                    topToTop = ConstraintSet.PARENT_ID
                }

                // 添加按钮
                textView("\uE6DC") {
                    id = R.id.anko_ib_add
                    backgroundResource = outValue.resourceId
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }.lparams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_import
                    topToTop = ConstraintSet.PARENT_ID
                }

                // 导入按钮
                textView("\uE6E2") {
                    id = R.id.anko_ib_import
                    backgroundResource = outValue.resourceId
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }.lparams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_share
                    topToTop = ConstraintSet.PARENT_ID
                }

                // 分享按钮
                textView("\uE6BA") {
                    id = R.id.anko_ib_share
                    backgroundResource = outValue.resourceId
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }.lparams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    endToStart = R.id.anko_ib_more
                    topToTop = ConstraintSet.PARENT_ID
                }

                textView("\uE6BF") {
                    id = R.id.anko_ib_more
                    backgroundResource = outValue.resourceId
                    textSize = 20f
                    gravity = Gravity.CENTER
                    includeFontPadding = false
                    typeface = iconFont
                }.lparams(dip(32), dip(32)) {
                    topMargin = statusBarMargin
                    marginEnd = dip(8)
                    endToEnd = ConstraintSet.PARENT_ID
                    topToTop = ConstraintSet.PARENT_ID
                }

                ViewUtils.createScheduleView(context!!)
                        .lparams(matchParent, 0) {
                            topToBottom = R.id.anko_tv_week
                            bottomToBottom = ConstraintSet.PARENT_ID
                            startToStart = ConstraintSet.PARENT_ID
                            endToEnd = ConstraintSet.PARENT_ID
                        }

            }
        }.view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MainFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
