package com.suda.yzune.wakeupschedule.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

class SquareTextView(context: Context, attributeSet: AttributeSet?) :
        TextView(context, attributeSet) {

    constructor(context: Context) : this(context, null)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }
}