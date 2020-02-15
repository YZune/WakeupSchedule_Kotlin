package com.suda.yzune.wakeupschedule.base_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.suda.yzune.wakeupschedule.R

abstract class BaseListFragment : BaseFragment() {

    protected lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ConstraintLayout(context).apply {

            mRecyclerView = RecyclerView(context, null, R.attr.verticalRecyclerViewStyle).apply {
                overScrollMode = View.OVER_SCROLL_NEVER
            }

            addView(mRecyclerView, ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT).apply {
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            })
        }

    }
}