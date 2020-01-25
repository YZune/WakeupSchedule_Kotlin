package com.suda.yzune.wakeupschedule.base_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import splitties.views.dsl.constraintlayout.constraintLayout
import splitties.views.dsl.constraintlayout.lParams
import splitties.views.dsl.core.add
import splitties.views.dsl.core.matchParent
import splitties.views.dsl.recyclerview.recyclerView

abstract class BaseListFragment : BaseFragment() {

    protected lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return context?.constraintLayout {

            mRecyclerView = recyclerView {
                overScrollMode = View.OVER_SCROLL_NEVER
            }

            add(mRecyclerView, lParams(matchParent, matchParent) {
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            })
        }

    }
}