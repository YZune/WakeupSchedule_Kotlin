package com.suda.yzune.wakeupschedule.widget.colorpicker

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.suda.yzune.wakeupschedule.R
import splitties.dimensions.dip

class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    /**
     * The width in px of the hue panel.
     */
    private var huePanelWidthPx: Int = 0
    /**
     * The height in px of the alpha panel
     */
    private var alphaPanelHeightPx: Int = 0
    /**
     * The distance in px between the different
     * color panels.
     */
    private var panelSpacingPx: Int = 0
    /**
     * The radius in px of the color palette tracker circle.
     */
    private var circleTrackerRadiusPx: Int = 0
    /**
     * The px which the tracker of the hue or alpha panel
     * will extend outside of its bounds.
     */
    private var sliderTrackerOffsetPx: Int = 0
    /**
     * Height of slider tracker on hue panel,
     * width of slider on alpha panel.
     */
    private var sliderTrackerSizePx: Int = 0

    private val satValPaint = Paint()
    private val satValTrackerPaint = Paint()

    private val alphaPaint = Paint()
    private val alphaTextPaint = Paint()
    private val hueAlphaTrackerPaint = Paint()

    private var valShader: Shader? = null
    private var satShader: Shader? = null
    private var alphaShader: Shader? = null

    /*
    * We cache a bitmap of the sat/val panel which is expensive to draw each time.
    * We can reuse it when the user is sliding the circle picker as long as the hue isn't changed.
    */
    private var satValBackgroundCache: BitmapCache? = null
    /* We cache the hue background to since its also very expensive now. */
    private var hueBackgroundCache: BitmapCache? = null

    /* Current values */
    private var alpha = 0xff
    private var hue = 360f
    private var sat = 0f
    private var `val` = 0f

    private var showAlphaPanel = false
    private var sliderTrackerColor = DEFAULT_SLIDER_COLOR

    /**
     * Minimum required padding. The offset from the
     * edge we must have or else the finger tracker will
     * get clipped when it's drawn outside of the view.
     */
    private var mRequiredPadding: Int = 0

    /**
     * The Rect in which we are allowed to draw.
     * Trackers can extend outside slightly,
     * due to the required padding we have set.
     */
    private var drawingRect: Rect? = null

    private var satValRect: Rect? = null
    private var hueRect: Rect? = null
    private var alphaRect: Rect? = null

    private var startTouchPoint: Point? = null

    private var alphaPatternDrawable: AlphaPatternDrawable? = null
    private var onColorChangedListener: OnColorChangedListener? = null

    /**
     * Get the current color this view is showing.
     *
     * @return the current color.
     */
    /**
     * Set the color the view should show.
     *
     * @param color The color that should be selected. #argb
     */
    var color: Int
        get() = Color.HSVToColor(alpha, floatArrayOf(hue, sat, `val`))
        set(color) = setColor(color, false)

    init {
        init(context, attrs)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val state = Bundle()
        state.putParcelable("instanceState", super.onSaveInstanceState())
        state.putInt("alpha", alpha)
        state.putFloat("hue", hue)
        state.putFloat("sat", sat)
        state.putFloat("val", `val`)
        state.putBoolean("show_alpha", showAlphaPanel)
        return state
    }

    public override fun onRestoreInstanceState(state: Parcelable?) {
        var mState = state
        if (mState is Bundle) {
            val bundle = mState as Bundle?

            alpha = bundle!!.getInt("alpha")
            hue = bundle.getFloat("hue")
            sat = bundle.getFloat("sat")
            `val` = bundle.getFloat("val")
            showAlphaPanel = bundle.getBoolean("show_alpha")

            mState = bundle.getParcelable("instanceState")
        }
        super.onRestoreInstanceState(mState)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        //Load those if set in xml resource file.
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPickerView)
        showAlphaPanel = a.getBoolean(R.styleable.ColorPickerView_cpv_alphaChannelVisible, false)
        sliderTrackerColor = a.getColor(R.styleable.ColorPickerView_cpv_sliderColor, -0x424243)
        a.recycle()

        applyThemeColors(context)

        huePanelWidthPx = dip(HUE_PANEL_WIDTH_DP)
        alphaPanelHeightPx = dip(ALPHA_PANEL_HEIGHT_DP)
        panelSpacingPx = dip(PANEL_SPACING_DP)
        circleTrackerRadiusPx = dip(CIRCLE_TRACKER_RADIUS_DP)
        sliderTrackerSizePx = dip(SLIDER_TRACKER_SIZE_DP)
        sliderTrackerOffsetPx = dip(SLIDER_TRACKER_OFFSET_DP)

        mRequiredPadding = dip(8)

        initPaintTools()

        //Needed for receiving trackball motion events.
        isFocusable = true
        isFocusableInTouchMode = true
    }

    private fun applyThemeColors(c: Context) {
        // If no specific border/slider color has been
        // set we take the default secondary text color
        // as border/slider color. Thus it will adopt
        // to theme changes automatically.

        val value = TypedValue()
        val a = c.obtainStyledAttributes(value.data, intArrayOf(android.R.attr.textColorSecondary))

        if (sliderTrackerColor == DEFAULT_SLIDER_COLOR) {
            sliderTrackerColor = a.getColor(0, DEFAULT_SLIDER_COLOR)
        }

        a.recycle()
    }

    private fun initPaintTools() {
        satValTrackerPaint.style = Paint.Style.FILL
        satValTrackerPaint.isAntiAlias = true

        hueAlphaTrackerPaint.color = sliderTrackerColor
        hueAlphaTrackerPaint.style = Paint.Style.FILL_AND_STROKE
        hueAlphaTrackerPaint.strokeWidth = dip(2).toFloat()
        hueAlphaTrackerPaint.isAntiAlias = true

        alphaTextPaint.color = -0xe3e3e4
        alphaTextPaint.textSize = 14f
        alphaTextPaint.isAntiAlias = true
        alphaTextPaint.textAlign = Paint.Align.CENTER
        alphaTextPaint.isFakeBoldText = true
    }

    override fun onDraw(canvas: Canvas) {
        if (drawingRect!!.width() <= 0 || drawingRect!!.height() <= 0) {
            return
        }

        drawSatValPanel(canvas)
        drawHuePanel(canvas)
        drawAlphaPanel(canvas)
    }

    private fun drawSatValPanel(canvas: Canvas) {
        val rect = satValRect

        if (valShader == null) {
            //Black gradient has either not been created or the view has been resized.
            valShader = LinearGradient(rect!!.left.toFloat(), rect.top.toFloat(), rect.left.toFloat(), rect.bottom.toFloat(), -0x1, -0x1000000, Shader.TileMode.CLAMP)
        }

        //If the hue has changed we need to recreate the cache.
        if (satValBackgroundCache == null || satValBackgroundCache!!.value != hue) {

            if (satValBackgroundCache == null) {
                satValBackgroundCache = BitmapCache()
            }

            //We create our bitmap in the cache if it doesn't exist.
            if (satValBackgroundCache?.bitmap == null) {
                satValBackgroundCache?.bitmap = Bitmap.createBitmap(rect!!.width(), rect.height(), Bitmap.Config.ARGB_8888)
            }

            //We create the canvas once so we can draw on our bitmap and the hold on to it.
            if (satValBackgroundCache?.canvas == null) {
                satValBackgroundCache?.canvas = Canvas(satValBackgroundCache?.bitmap!!)
            }

            val rgb = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))

            satShader = LinearGradient(rect!!.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.top.toFloat(), -0x1, rgb, Shader.TileMode.CLAMP)

            val mShader = ComposeShader(valShader!!, satShader!!, PorterDuff.Mode.MULTIPLY)
            satValPaint.shader = mShader

            // Finally we draw on our canvas, the result will be
            // stored in our bitmap which is already in the cache.
            // Since this is drawn on a canvas not rendered on
            // screen it will automatically not be using the
            // hardware acceleration. And this was the code that
            // wasn't supported by hardware acceleration which mean
            // there is no need to turn it of anymore. The rest of
            // the view will still be hw accelerated.
            satValBackgroundCache?.canvas?.drawRect(0f, 0f, satValBackgroundCache!!.bitmap!!.width.toFloat(),
                    satValBackgroundCache!!.bitmap!!.height.toFloat(), satValPaint)

            //We set the hue value in our cache to which hue it was drawn with,
            //then we know that if it hasn't changed we can reuse our cached bitmap.
            satValBackgroundCache?.value = hue
        }

        // We draw our bitmap from the cached, if the hue has changed
        // then it was just recreated otherwise the old one will be used.
        canvas.drawBitmap(satValBackgroundCache!!.bitmap!!, null, rect!!, null)

        val p = satValToPoint(sat, `val`)

        satValTrackerPaint.color = Color.WHITE
        canvas.drawCircle(p.x.toFloat(), p.y.toFloat(), circleTrackerRadiusPx.toFloat(), satValTrackerPaint)
    }

    private fun drawHuePanel(canvas: Canvas) {
        val rect = hueRect

        if (hueBackgroundCache == null) {
            hueBackgroundCache = BitmapCache()
            hueBackgroundCache!!.bitmap = Bitmap.createBitmap(rect!!.width(), rect.height(), Bitmap.Config.ARGB_8888)
            hueBackgroundCache!!.canvas = Canvas(hueBackgroundCache!!.bitmap!!)

            val hueColors = IntArray((rect.height() + 0.5f).toInt())

            // Generate array of all colors, will be drawn as individual lines.
            var h = 360f
            for (i in hueColors.indices) {
                hueColors[i] = Color.HSVToColor(floatArrayOf(h, 1f, 1f))
                h -= 360f / hueColors.size
            }

            // Time to draw the hue color gradient,
            // its drawn as individual lines which
            // will be quite many when the resolution is high
            // and/or the panel is large.
            val linePaint = Paint()
            linePaint.strokeWidth = 0f
            for (i in hueColors.indices) {
                linePaint.color = hueColors[i]
                hueBackgroundCache!!.canvas!!.drawLine(0f, i.toFloat(), hueBackgroundCache!!.bitmap!!.width.toFloat(), i.toFloat(), linePaint)
            }
        }

        canvas.drawBitmap(hueBackgroundCache!!.bitmap!!, null, rect!!, null)

        val p = hueToPoint(hue)

        val r = RectF()
        r.left = (rect.left - sliderTrackerOffsetPx).toFloat()
        r.right = (rect.right + sliderTrackerOffsetPx).toFloat()
        r.top = (p.y - sliderTrackerSizePx / 2).toFloat()
        r.bottom = (p.y + sliderTrackerSizePx / 2).toFloat()

        canvas.drawRoundRect(r, 2f, 2f, hueAlphaTrackerPaint)
    }

    private fun drawAlphaPanel(canvas: Canvas) {
        /*
     * Will be drawn with hw acceleration, very fast.
     * Also the AlphaPatternDrawable is backed by a bitmap
     * generated only once if the size does not change.
     */

        if (!showAlphaPanel || alphaRect == null || alphaPatternDrawable == null) return

        val rect = alphaRect

        alphaPatternDrawable!!.draw(canvas)

        val hsv = floatArrayOf(hue, sat, `val`)
        val color = Color.HSVToColor(hsv)
        val aColor = Color.HSVToColor(0, hsv)

        alphaShader = LinearGradient(rect!!.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.top.toFloat(), color, aColor, Shader.TileMode.CLAMP)

        alphaPaint.shader = alphaShader

        canvas.drawRect(rect, alphaPaint)

        val p = alphaToPoint(alpha)

        val r = RectF()
        r.left = (p.x - sliderTrackerSizePx / 2).toFloat()
        r.right = (p.x + sliderTrackerSizePx / 2).toFloat()
        r.top = (rect.top - sliderTrackerOffsetPx).toFloat()
        r.bottom = (rect.bottom + sliderTrackerOffsetPx).toFloat()

        canvas.drawRoundRect(r, 2f, 2f, hueAlphaTrackerPaint)
    }

    private fun hueToPoint(hue: Float): Point {

        val rect = hueRect
        val height = rect!!.height().toFloat()

        val p = Point()

        p.y = (height - hue * height / 360f + rect.top).toInt()
        p.x = rect.left

        return p
    }

    private fun satValToPoint(sat: Float, `val`: Float): Point {

        val rect = satValRect
        val height = rect!!.height().toFloat()
        val width = rect.width().toFloat()

        val p = Point()

        p.x = (sat * width + rect.left).toInt()
        p.y = ((1f - `val`) * height + rect.top).toInt()

        return p
    }

    private fun alphaToPoint(alpha: Int): Point {

        val rect = alphaRect
        val width = rect!!.width().toFloat()

        val p = Point()

        p.x = (width - alpha * width / 0xff + rect.left).toInt()
        p.y = rect.top

        return p
    }

    private fun pointToSatVal(x: Float, y: Float): FloatArray {
        var x = x
        var y = y

        val rect = satValRect
        val result = FloatArray(2)

        val width = rect!!.width().toFloat()
        val height = rect.height().toFloat()

        when {
            x < rect.left -> x = 0f
            x > rect.right -> x = width
            else -> x -= rect.left
        }

        when {
            y < rect.top -> y = 0f
            y > rect.bottom -> y = height
            else -> y -= rect.top
        }

        result[0] = 1f / width * x
        result[1] = 1f - 1f / height * y

        return result
    }

    private fun pointToHue(f: Float): Float {
        var y = f

        val rect = hueRect

        val height = rect!!.height().toFloat()

        when {
            y < rect.top -> y = 0f
            y > rect.bottom -> y = height
            else -> y -= rect.top
        }

        return 360f - y * 360f / height
    }

    private fun pointToAlpha(i: Int): Int {
        var x = i

        val rect = alphaRect
        val width = rect!!.width()

        when {
            x < rect.left -> x = 0
            x > rect.right -> x = width
            else -> x -= rect.left
        }

        return 0xff - x * 0xff / width
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var update = false

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                startTouchPoint = Point(event.x.toInt(), event.y.toInt())
                update = moveTrackersIfNeeded(event)
            }
            MotionEvent.ACTION_MOVE -> update = moveTrackersIfNeeded(event)
            MotionEvent.ACTION_UP -> {
                startTouchPoint = null
                update = moveTrackersIfNeeded(event)
            }
        }

        if (update) {
            if (onColorChangedListener != null) {
                onColorChangedListener!!.onColorChanged(Color.HSVToColor(alpha, floatArrayOf(hue, sat, `val`)))
            }
            invalidate()
            return true
        }

        return super.onTouchEvent(event)
    }

    private fun moveTrackersIfNeeded(event: MotionEvent): Boolean {
        if (startTouchPoint == null) {
            return false
        }

        var update = false

        val startX = startTouchPoint!!.x
        val startY = startTouchPoint!!.y

        if (hueRect!!.contains(startX, startY)) {
            hue = pointToHue(event.y)

            update = true
        } else if (satValRect!!.contains(startX, startY)) {
            val result = pointToSatVal(event.x, event.y)

            sat = result[0]
            `val` = result[1]

            update = true
        } else if (alphaRect != null && alphaRect!!.contains(startX, startY)) {
            alpha = pointToAlpha(event.x.toInt())

            update = true
        }

        return update
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val finalWidth: Int
        val finalHeight: Int

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        val widthAllowed = View.MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val heightAllowed = View.MeasureSpec.getSize(heightMeasureSpec) - paddingBottom - paddingTop

        if (widthMode == View.MeasureSpec.EXACTLY || heightMode == View.MeasureSpec.EXACTLY) {
            //A exact value has been set in either direction, we need to stay within this size.

            if (widthMode == View.MeasureSpec.EXACTLY && heightMode != View.MeasureSpec.EXACTLY) {
                //The with has been specified exactly, we need to adopt the height to fit.
                var h = widthAllowed - panelSpacingPx - huePanelWidthPx

                if (showAlphaPanel) {
                    h += panelSpacingPx + alphaPanelHeightPx
                }

                finalHeight = if (h > heightAllowed) {
                    //We can't fit the view in this container, set the size to whatever was allowed.
                    heightAllowed
                } else {
                    h
                }

                finalWidth = widthAllowed
            } else if (heightMode == View.MeasureSpec.EXACTLY && widthMode != View.MeasureSpec.EXACTLY) {
                //The height has been specified exactly, we need to stay within this height and adopt the width.

                var w = heightAllowed + panelSpacingPx + huePanelWidthPx

                if (showAlphaPanel) {
                    w -= panelSpacingPx + alphaPanelHeightPx
                }

                finalWidth = if (w > widthAllowed) {
                    //we can't fit within this container, set the size to whatever was allowed.
                    widthAllowed
                } else {
                    w
                }

                finalHeight = heightAllowed
            } else {
                //If we get here the dev has set the width and height to exact sizes. For example match_parent or 300dp.
                //This will mean that the sat/val panel will not be square but it doesn't matter. It will work anyway.
                //In all other senarios our goal is to make that panel square.

                //We set the sizes to exactly what we were told.
                finalWidth = widthAllowed
                finalHeight = heightAllowed
            }
        } else {
            //If no exact size has been set we try to make our view as big as possible
            //within the allowed space.

            //Calculate the needed width to layout using max allowed height.
            var widthNeeded = heightAllowed + panelSpacingPx + huePanelWidthPx

            //Calculate the needed height to layout using max allowed width.
            var heightNeeded = widthAllowed - panelSpacingPx - huePanelWidthPx

            if (showAlphaPanel) {
                widthNeeded -= panelSpacingPx + alphaPanelHeightPx
                heightNeeded += panelSpacingPx + alphaPanelHeightPx
            }

            var widthOk = false
            var heightOk = false

            if (widthNeeded <= widthAllowed) {
                widthOk = true
            }

            if (heightNeeded <= heightAllowed) {
                heightOk = true
            }

            if (widthOk && heightOk) {
                finalWidth = widthAllowed
                finalHeight = heightNeeded
            } else if (!heightOk && widthOk) {
                finalHeight = heightAllowed
                finalWidth = widthNeeded
            } else if (!widthOk && heightOk) {
                finalHeight = heightNeeded
                finalWidth = widthAllowed
            } else {
                finalHeight = heightAllowed
                finalWidth = widthAllowed
            }
        }

        setMeasuredDimension(finalWidth + paddingLeft + paddingRight,
                finalHeight + paddingTop + paddingBottom)
    }

    override fun getPaddingTop(): Int {
        return Math.max(super.getPaddingTop(), mRequiredPadding)
    }

    override fun getPaddingBottom(): Int {
        return Math.max(super.getPaddingBottom(), mRequiredPadding)
    }

    override fun getPaddingLeft(): Int {
        return Math.max(super.getPaddingLeft(), mRequiredPadding)
    }

    override fun getPaddingRight(): Int {
        return Math.max(super.getPaddingRight(), mRequiredPadding)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawingRect = Rect()
        drawingRect!!.left = paddingLeft
        drawingRect!!.right = w - paddingRight
        drawingRect!!.top = paddingTop
        drawingRect!!.bottom = h - paddingBottom

        //The need to be recreated because they depend on the size of the view.
        valShader = null
        satShader = null
        alphaShader = null

        // Clear those bitmap caches since the size may have changed.
        satValBackgroundCache = null
        hueBackgroundCache = null

        setUpSatValRect()
        setUpHueRect()
        setUpAlphaRect()
    }

    private fun setUpSatValRect() {
        //Calculate the size for the big color rectangle.
        val dRect = drawingRect

        val left = dRect!!.left
        val top = dRect.top
        var bottom = dRect.bottom
        val right = dRect.right - panelSpacingPx - huePanelWidthPx

        if (showAlphaPanel) {
            bottom -= alphaPanelHeightPx + panelSpacingPx
        }

        satValRect = Rect(left, top, right, bottom)
    }

    private fun setUpHueRect() {
        //Calculate the size for the hue slider on the left.
        val dRect = drawingRect

        val left = dRect!!.right - huePanelWidthPx
        val top = dRect.top
        val bottom = dRect.bottom - if (showAlphaPanel) panelSpacingPx + alphaPanelHeightPx else 0
        val right = dRect.right

        hueRect = Rect(left, top, right, bottom)
    }

    private fun setUpAlphaRect() {

        if (!showAlphaPanel) return

        val dRect = drawingRect

        val left = dRect!!.left
        val top = dRect.bottom - alphaPanelHeightPx
        val bottom = dRect.bottom
        val right = dRect.right

        alphaRect = Rect(left, top, right, bottom)

        alphaPatternDrawable = AlphaPatternDrawable(dip(4))
        alphaPatternDrawable!!.setBounds(Math.round(alphaRect!!.left.toFloat()), Math.round(alphaRect!!.top.toFloat()), Math.round(alphaRect!!.right.toFloat()),
                Math.round(alphaRect!!.bottom.toFloat()))
    }

    /**
     * Set a OnColorChangedListener to get notified when the color
     * selected by the user has changed.
     *
     * @param listener the listener
     */
    fun setOnColorChangedListener(listener: OnColorChangedListener) {
        onColorChangedListener = listener
    }

    /**
     * Set the color this view should show.
     *
     * @param color The color that should be selected. #argb
     * @param callback If you want to get a callback to your OnColorChangedListener.
     */
    fun setColor(color: Int, callback: Boolean) {

        val alpha = Color.alpha(color)
        val red = Color.red(color)
        val blue = Color.blue(color)
        val green = Color.green(color)

        val hsv = FloatArray(3)

        Color.RGBToHSV(red, green, blue, hsv)

        this.alpha = alpha
        hue = hsv[0]
        sat = hsv[1]
        `val` = hsv[2]

        if (callback && onColorChangedListener != null) {
            onColorChangedListener!!.onColorChanged(Color.HSVToColor(this.alpha, floatArrayOf(hue, sat, `val`)))
        }

        invalidate()
    }

    fun setAlphaSliderVisible(visible: Boolean) {
        if (showAlphaPanel != visible) {
            showAlphaPanel = visible
            valShader = null
            satShader = null
            alphaShader = null
            hueBackgroundCache = null
            satValBackgroundCache = null
            requestLayout()
        }
    }

    interface OnColorChangedListener {
        fun onColorChanged(newColor: Int)
    }

    private inner class BitmapCache {
        var canvas: Canvas? = null
        var bitmap: Bitmap? = null
        var value: Float = 0f
    }

    companion object {
        private const val DEFAULT_SLIDER_COLOR = -0x424243
        private const val HUE_PANEL_WIDTH_DP = 32
        private const val ALPHA_PANEL_HEIGHT_DP = 8
        private const val PANEL_SPACING_DP = 16
        private const val CIRCLE_TRACKER_RADIUS_DP = 8
        private const val SLIDER_TRACKER_SIZE_DP = 8
        private const val SLIDER_TRACKER_OFFSET_DP = 4
    }
}