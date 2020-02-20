package com.suda.yzune.wakeupschedule.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Environment
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.HtmlCompat
import com.suda.yzune.wakeupschedule.R
import java.io.File
import java.io.FileOutputStream

object ViewUtils {

    fun judgeColorIsLight(color: Int): Boolean {
        val red = color and 0xff0000 shr 16
        val green = color and 0x00ff00 shr 8
        val blue = color and 0x0000ff
        return (0.213 * red + 0.715 * green + 0.072 * blue > 255 / 2)
    }

    fun getScreenInfo(context: Context): Array<Int> {
        val displayMetrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return arrayOf(width, height)
    }

    fun getHtmlSpannedString(str: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(str, HtmlCompat.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(str)
        }
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun resizeStatusBar(context: Context, view: View) {
        val layoutParams = view.layoutParams
        layoutParams.height = getStatusBarHeight(context.applicationContext)
        view.layoutParams = layoutParams
    }

    /**
     * 获取是否存在NavigationBar
     * @param context
     * @return
     */
    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {

        }

        return hasNavigationBar
    }

    /**
     * 获取虚拟功能键高度
     * @param context
     * @return
     */
    fun getVirtualBarHeight(context: Context): Int {
        var vh = 0
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val dm = DisplayMetrics()
        try {
            val c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            vh = dm.heightPixels - windowManager.defaultDisplay.height
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vh
    }

    fun createColorStateList(color: Int): ColorStateList {
        val colors = intArrayOf(color, color, color, color, color, color)
        val states = arrayOfNulls<IntArray>(6)
        states[0] = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        states[1] = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_focused)
        states[2] = intArrayOf(android.R.attr.state_enabled)
        states[3] = intArrayOf(android.R.attr.state_focused)
        states[4] = intArrayOf(android.R.attr.state_window_focused)
        states[5] = intArrayOf()
        return ColorStateList(states, colors)
    }

    fun createColorStateList(normal: Int, pressed: Int, focused: Int, unable: Int): ColorStateList {
        val colors = intArrayOf(pressed, focused, normal, focused, unable, normal)
        val states = arrayOfNulls<IntArray>(6)
        states[0] = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        states[1] = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_focused)
        states[2] = intArrayOf(android.R.attr.state_enabled)
        states[3] = intArrayOf(android.R.attr.state_focused)
        states[4] = intArrayOf(android.R.attr.state_window_focused)
        states[5] = intArrayOf()
        return ColorStateList(states, colors)
    }

    fun getRealSize(activity: Activity): Point {
        val size = Point()
        activity.windowManager.defaultDisplay.getRealSize(size)
        return size
    }

    fun saveImg(bitmap: Bitmap) {
        //把图片存储在哪个文件夹
        val file = File(Environment.getExternalStorageDirectory(), "DCIM")
        if (!file.exists()) {
            file.mkdir()
        }
        //图片的名称
        val name = "mz.jpg"
        val file1 = File(file, name)
        if (!file1.exists()) {
            try {
                val fileOutputStream = FileOutputStream(file1)
                //这个100表示压缩比,100说明不压缩,90说明压缩到原来的90%
                //注意:这是对于占用存储空间而言,不是说占用内存的大小
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        //通知图库即使更新,否则不能看到图片
        //activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file1.getAbsolutePath())));
    }

    fun getViewBitmap(viewGroup: ViewGroup, low: Boolean = false, marginBottom: Int = 0): Bitmap {
        var h = 0
        val bitmap: Bitmap
        // 获取scrollView实际高度,这里很重要
        for (i in 0 until viewGroup.childCount) {
            h += viewGroup.getChildAt(i).height + marginBottom
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(viewGroup.width, h,
                if (low) Bitmap.Config.ARGB_4444 else Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        viewGroup.draw(canvas)
        return bitmap
    }


    fun layoutView(v: View, width: Int, height: Int) {
        // validate view.width and view.height
        v.layout(0, 0, width, height)
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        // validate view.measurewidth and view.measureheight
        v.measure(measuredWidth, measuredHeight)
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
    }

    fun getCustomizedColor(context: Context, index: Int): Int {
        val customizedColors = context.resources.getIntArray(R.array.customizedColors)
        return customizedColors[index]
    }
}
