package com.suda.yzune.wakeupschedule.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class SelectedRecyclerView(context: Context, attr: AttributeSet?, int: Int) :
        RecyclerView(context, attr, int) {

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    var positionChangedListener: PositionChangedListener? = null

    private var spanCount = 1

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        if (layout is StaggeredGridLayoutManager) {
            spanCount = (layoutManager as StaggeredGridLayoutManager).spanCount
        }
    }

    private fun calPosition(x: Int, y: Int): Int {
        return x / (width / spanCount) + y / (width / spanCount) * spanCount
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            if (e.x > width || e.x < 0 || e.y > height || e.y < 0) return true
            val pos = calPosition(e.x.toInt(), e.y.toInt())
            positionChangedListener?.changeState(pos, true)
        } else if (e.action == MotionEvent.ACTION_MOVE ||
                e.action == MotionEvent.ACTION_UP
        ) {
            if (e.x > width || e.x < 0 || e.y > height || e.y < 0) return true
            val pos = calPosition(e.x.toInt(), e.y.toInt())
            positionChangedListener?.changeState(pos, false)
        }
        return true
    }

    interface PositionChangedListener {
        fun changeState(pos: Int, isDown: Boolean)
    }
}