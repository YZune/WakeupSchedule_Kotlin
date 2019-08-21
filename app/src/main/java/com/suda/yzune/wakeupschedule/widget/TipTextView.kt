package com.suda.yzune.wakeupschedule.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint.Style
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

import org.jetbrains.anko.dip


class TipTextView : AppCompatTextView {

    private val path = Path()

    var tipVisibility = 0
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            tipVisibility = TIP_INVISIBLE
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (tipVisibility == 1) {
            paint.isAntiAlias = true
            paint.isDither = true
            paint.style = Style.FILL_AND_STROKE

            path.moveTo(width - dip(12).toFloat(), height - dip(6).toFloat()) // 此点为多边形的起点
            path.lineTo(width - dip(6).toFloat(), height - dip(6).toFloat())
            path.lineTo(width - dip(6).toFloat(), height - dip(12).toFloat())
            path.close() // 使这些点构成封闭的多边形
            canvas.drawPath(path, paint)
        }
    }

    companion object {
        const val TIP_INVISIBLE = 0
        const val TIP_VISIBLE = 1
    }
}
