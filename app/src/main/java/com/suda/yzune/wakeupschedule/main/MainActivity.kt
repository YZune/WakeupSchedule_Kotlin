package com.suda.yzune.wakeupschedule.main

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseActivity
import com.suda.yzune.wakeupschedule.dao.TimeTableDao
import com.suda.yzune.wakeupschedule.schedule.ScheduleActivityUI
import com.suda.yzune.wakeupschedule.schedule.ScheduleViewModel
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.find
import org.jetbrains.anko.setContentView
import kotlin.math.abs

class MainActivity : BaseActivity() {

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var viewPager: ViewPager
    private lateinit var bgImageView: ImageView
    private lateinit var blurImageView: ImageView

    private lateinit var listener: ViewPager.OnPageChangeListener

    private var index = 0
    private var width = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        super.onCreate(savedInstanceState)
        MainActivityUI().setContentView(this)
        initView()
    }

    private fun initView() {
        viewPager = find(R.id.anko_vp_schedule)
        bgImageView = find(R.id.anko_iv_bg)
        blurImageView = find(R.id.anko_iv_blur)

        launch {
            viewModel.table = withContext(Dispatchers.IO) {
                viewModel.getDefaultTable()
            }

            val x = (ViewUtils.getRealSize(this@MainActivity).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this@MainActivity).y * 0.5).toInt()
            Glide.with(this@MainActivity)
                    .load(R.drawable.main_background_2019)
                    .override(x, y)
                    .into(bgImageView)
            Glide.with(this@MainActivity)
                    .load(R.drawable.main_background_2019)
                    .override(x, y)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 5)))
                    .into(blurImageView)

            initEvent()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvent() {
        viewPager.adapter =
                object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                    override fun getItem(position: Int): Fragment {
                        return MainFragment.newInstance("", "")
                    }

                    override fun getCount(): Int {
                        return 3
                    }

                }

        listener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == 0) {
                    ObjectAnimator.ofFloat(blurImageView, "alpha", if (index == 1) 0f else 1f).start()
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                ObjectAnimator.ofFloat(blurImageView, "alpha", if (position == 1) 0f else 1f).start()
                index = position
            }
        }

        viewPager.addOnPageChangeListener(listener)

        viewPager.currentItem = 1

        val windowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        width = size.x

        viewPager.setOnTouchListener(
                object : View.OnTouchListener {
                    var startX = 0f
                    override fun onTouch(view: View, event: MotionEvent): Boolean {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                startX = event.x
                            }
                            MotionEvent.ACTION_MOVE -> {
                                if (index == 1) {
                                    if (abs(startX - event.x) >= width / 20) {
                                        blurImageView.alpha = (abs(startX - event.x) - width / 20) * 1f / (width / 2)
                                    }
                                } else if (index == 2) {
                                    if (event.x - startX >= width / 20) {
                                        blurImageView.alpha = 1 - (event.x - startX - width / 20) * 1f / (width / 2)
                                    }
                                } else if (index == 0) {
                                    if (startX - event.x >= width / 20) {
                                        blurImageView.alpha = 1 - (startX - event.x - width / 20) * 1f / (width / 2)
                                    }
                                }
                            }
                        }
                        return false
                    }
                }
        )
    }

    override fun onDestroy() {
        viewPager.removeOnPageChangeListener(listener)
        super.onDestroy()
    }

}




