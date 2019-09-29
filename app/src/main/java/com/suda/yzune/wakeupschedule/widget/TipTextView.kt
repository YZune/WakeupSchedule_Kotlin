package com.suda.yzune.wakeupschedule.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import org.jetbrains.anko.dip


@SuppressLint("ViewConstructor")
class TipTextView(mColor: Int, mSize: Int, context: Context) : View(context) {

    var text: String = ""

    var tipVisibility = 0
        set(value) {
            field = value
            invalidate()
        }

    private val mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = mSize * resources.displayMetrics.density
        typeface = Typeface.DEFAULT_BOLD
        color = mColor
    }
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = mColor
        isDither = true
        style = Paint.Style.FILL_AND_STROKE
    }
    private val path = Path()
    private var mStaticLayout: StaticLayout? = null

    fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            tipVisibility = TIP_INVISIBLE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Tell the parent layout how big this view would like to be
        // but still respect any requirements (measure specs) that are passed down.

        // determine the width
        val width = MeasureSpec.getSize(widthMeasureSpec)

        // determine the height
        val height = MeasureSpec.getSize(heightMeasureSpec)

        // Required call: set width and height
        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mStaticLayout == null) {
            mStaticLayout = StaticLayout(
                    text,
                    mTextPaint,
                    width - paddingRight - paddingLeft,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0f,
                    false
            )
        }
        if (tipVisibility == 1) {
            path.moveTo(width - dip(12).toFloat(), height - dip(6).toFloat()) // 此点为多边形的起点
            path.lineTo(width - dip(6).toFloat(), height - dip(6).toFloat())
            path.lineTo(width - dip(6).toFloat(), height - dip(12).toFloat())
            path.close() // 使这些点构成封闭的多边形
            canvas.drawPath(path, mPaint)
        }
        canvas.save()
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        mStaticLayout!!.draw(canvas)
        canvas.restore()
    }

    companion object {
        const val TIP_INVISIBLE = 0
        const val TIP_VISIBLE = 1
    }
}
