package com.suda.yzune.wakeupschedule.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.suda.yzune.wakeupschedule.R
import splitties.dimensions.dip

class RoomView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    private val paint = Paint()
    private val textPaint = Paint()
    var list = listOf<String>()
        set(value) {
            field = value
            invalidate()
        }

    init {
        textPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12f, resources.displayMetrics)
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val a = (measuredWidth - dip(84)) / 12f
        val fontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        var eachLeft = 0f
        for (i in 1..12) {
            if (i.toString() in list) {
                textPaint.color = Color.WHITE
                paint.color = ContextCompat.getColor(context, R.color.colorAccent)
            } else {
                textPaint.color = Color.GRAY
                paint.color = Color.parseColor("#ffeeff")
            }
            canvas.drawRect(eachLeft, 0f, eachLeft + a, height.toFloat(), paint)
            canvas.drawText(i.toString(), eachLeft + a / 2, height / 2 + distance, textPaint)
            if (i % 4 == 0 && i != 12) {
                eachLeft += a + dip(24)
            } else if (i % 12 != 0) {
                eachLeft += a + dip(4)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, ((width - dip(84)) / 12f).toInt())
    }

}