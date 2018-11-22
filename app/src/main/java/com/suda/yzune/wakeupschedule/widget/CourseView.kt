package com.suda.yzune.wakeupschedule.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class CourseView(context: Context) : View(context) {
    private lateinit var mPaint: Paint
    private val text = "高等数学"

    init {
        initPaint()
    }

    /**
     * 初始化画笔设置
     */
    private fun initPaint() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = Color.parseColor("#FF4081")
        mPaint.textSize = 90f
    }

    /**
     * 绘制
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(text, 0f, (height / 2).toFloat(), mPaint)
    }

    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val wSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val wSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val hSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val hSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)

        if (wSpecMode == View.MeasureSpec.AT_MOST && hSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300)
        } else if (wSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, hSpecSize)
        } else if (hSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(wSpecSize, 300)
        }
    }
}

