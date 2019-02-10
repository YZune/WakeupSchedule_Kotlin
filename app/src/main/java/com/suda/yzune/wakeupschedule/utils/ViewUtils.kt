package com.suda.yzune.wakeupschedule.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.HtmlCompat
import com.suda.yzune.wakeupschedule.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import java.io.File
import java.io.FileOutputStream

object ViewUtils {

    fun judgeColorIsLight(color: Int): Boolean {
        val red = color and 0xff0000 shr 16
        val green = color and 0x00ff00 shr 8
        val blue = color and 0x0000ff
        return (0.213 * red + 0.715 * green + 0.072 * blue > 255 / 2)
    }

    fun createScheduleView(context: Context): View {
        return context.UI {
            constraintLayout {
                for (i in 0..8) {
                    textView {
                        id = R.id.anko_tv_title0 + i
                        setPadding(0, dip(8), 0, dip(8))
                        textSize = 12f
                        gravity = Gravity.CENTER
                        setLineSpacing(dip(2).toFloat(), 1f)
                        if (i == 0) {
                            typeface = Typeface.DEFAULT_BOLD
                        }
                    }.lparams(0, wrapContent) {
                        when (i) {
                            0 -> {
                                horizontalWeight = 0.5f
                                startToStart = ConstraintSet.PARENT_ID
                                topToTop = ConstraintSet.PARENT_ID
                                endToStart = R.id.anko_tv_title0 + i + 1
                            }
                            8 -> {
                                horizontalWeight = 1f
                                startToEnd = R.id.anko_tv_title0 + i - 1
                                endToEnd = ConstraintSet.PARENT_ID
                                baselineToBaseline = R.id.anko_tv_title0 + i - 1
                            }
                            else -> {
                                horizontalWeight = 1f
                                startToEnd = R.id.anko_tv_title0 + i - 1
                                endToStart = R.id.anko_tv_title0 + i + 1
                                baselineToBaseline = R.id.anko_tv_title0 + i - 1
                            }
                        }
                    }
                }
                scrollView {
                    id = R.id.anko_sv_schedule
                    overScrollMode = View.OVER_SCROLL_NEVER
                    isVerticalScrollBarEnabled = false
                    constraintLayout {
                        id = R.id.anko_cl_content_panel
                        for (i in 1..20) {
                            textView(i.toString()) {
                                id = R.id.anko_tv_node1 + i - 1
                                textSize = 12f
                                gravity = Gravity.CENTER
                            }.lparams(0, dip(56)) {
                                topMargin = dip(2)
                                when (i) {
                                    1 -> {
                                        bottomToTop = R.id.anko_tv_node1 + i
                                        endToStart = R.id.anko_ll_week_panel_0
                                        horizontalWeight = 0.5f
                                        startToStart = ConstraintSet.PARENT_ID
                                        topToTop = ConstraintSet.PARENT_ID
                                        verticalBias = 0f
                                        verticalChainStyle = ConstraintSet.CHAIN_PACKED
                                    }
                                    20 -> {
                                        bottomToTop = R.id.anko_navigation_bar_view
                                        endToStart = R.id.anko_ll_week_panel_0
                                        horizontalWeight = 0.5f
                                        startToStart = ConstraintSet.PARENT_ID
                                        topToBottom = R.id.anko_tv_node1 + i - 2
                                    }
                                    else -> {
                                        bottomToTop = R.id.anko_tv_node1 + i
                                        endToStart = R.id.anko_ll_week_panel_0
                                        horizontalWeight = 0.5f
                                        startToStart = ConstraintSet.PARENT_ID
                                        topToBottom = R.id.anko_tv_node1 + i - 2
                                    }
                                }
                            }
                        }
                        val barHeight = if (ViewUtils.getVirtualBarHeight(context) in 1..48) {
                            ViewUtils.getVirtualBarHeight(context)
                        } else {
                            dip(48)
                        }
                        val navBar = view {
                            id = R.id.anko_navigation_bar_view
                        }.lparams(matchParent, barHeight) {
                            topToBottom = R.id.anko_tv_node992
                            bottomToBottom = ConstraintSet.PARENT_ID
                            startToStart = ConstraintSet.PARENT_ID
                            endToEnd = ConstraintSet.PARENT_ID
                        }
                        if (PreferenceUtils.getBooleanFromSP(context, "hide_main_nav_bar", false) && Build.VERSION.SDK_INT >= 19) {
                            navBar.visibility = View.VISIBLE
                        } else {
                            navBar.visibility = View.GONE
                        }
                        for (i in 0..7) {
                            verticalLayout {
                                id = R.id.anko_ll_week_panel_0 + i
                            }.lparams(0, wrapContent) {
                                marginStart = dip(1)
                                marginEnd = dip(1)
                                horizontalWeight = 1f
                                when (i) {
                                    0 -> {
                                        startToEnd = R.id.anko_tv_node1
                                        endToStart = R.id.anko_ll_week_panel_0 + i + 1
                                    }
                                    7 -> {
                                        startToEnd = R.id.anko_ll_week_panel_0 + i - 1
                                        endToEnd = ConstraintSet.PARENT_ID
                                    }
                                    else -> {
                                        startToEnd = R.id.anko_ll_week_panel_0 + i - 1
                                        endToStart = R.id.anko_ll_week_panel_0 + i + 1
                                    }
                                }
                            }
                        }
                    }
                }.lparams(matchParent, 0) {
                    bottomToBottom = ConstraintSet.PARENT_ID
                    topToBottom = R.id.anko_tv_title0
                }
            }
        }.view

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
        layoutParams.height = ViewUtils.getStatusBarHeight(context.applicationContext)
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

    fun getViewBitmap(scrollView: ScrollView): Bitmap {
        var h = 0
        val bitmap: Bitmap
        // 获取scrollView实际高度,这里很重要
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.width, h,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)
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
