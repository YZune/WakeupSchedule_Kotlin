package com.suda.yzune.wakeupschedule.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseActivity
import com.suda.yzune.wakeupschedule.schedule.AfterImportTipFragment
import com.suda.yzune.wakeupschedule.schedule.ScheduleViewModel
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import jp.wasabeef.glide.transformations.BlurTransformation
import splitties.dimensions.dip
import kotlin.math.abs
import kotlin.math.max

class MainActivity : BaseActivity() {

    private lateinit var viewModel: ScheduleViewModel
    lateinit var viewPager: ViewPager
    private lateinit var bgImageView: ImageView
    private lateinit var blurImageView: ImageView

    private lateinit var listener: ViewPager.OnPageChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        super.onCreate(savedInstanceState)
        viewModel.statusBarMargin = getStatusBarHeight() + dip(8)
        setContentView(MainActivityUI(this).root)
        initView()
    }

    private fun initView() {
        viewPager = findViewById(R.id.anko_vp_schedule)
        bgImageView = findViewById(R.id.anko_iv_bg)
        blurImageView = findViewById(R.id.anko_iv_blur)

        launch {
            viewModel.table = viewModel.getDefaultTable()

            initTheme()

            initEvent()

            viewModel.timeList = viewModel.getTimeList(viewModel.table.timeTable)

            for (i in 1..7) {
                viewModel.getRawCourseByDay(i, viewModel.table.id).observe(this@MainActivity, Observer { list ->
                    if (list == null) return@Observer
                    if (list.isNotEmpty() && list[0].tableId != viewModel.table.id) return@Observer
                    viewModel.allCourseList[i - 1].value = list
                })
            }
        }
    }

    private fun initTheme() {
        if (viewModel.table.background != "") {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            Glide.with(this@MainActivity)
                    .load(viewModel.table.background)
                    .override(x, y)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(bgImageView)
            Glide.with(this@MainActivity)
                    .load(viewModel.table.background)
                    .override(x, y)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 5)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(blurImageView)
        } else {
            val x = (ViewUtils.getRealSize(this).x * 0.5).toInt()
            val y = (ViewUtils.getRealSize(this).y * 0.5).toInt()
            Glide.with(this@MainActivity)
                    .load(R.drawable.main_background_2019)
                    .override(x, y)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(bgImageView)
            Glide.with(this@MainActivity)
                    .load(R.drawable.main_background_2019)
                    .override(x, y)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 5)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(blurImageView)
        }

        if (ViewUtils.judgeColorIsLight(viewModel.table.textColor)) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }

        viewModel.itemHeight = dip(viewModel.table.itemHeight)
    }

    private fun initEvent() {
        viewPager.offscreenPageLimit = 3

        viewPager.adapter =
                object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                    override fun getItem(position: Int): Fragment {
                        when (position) {
                            0 -> return DashBoardFragment.newInstance()
                            1 -> MainFragment.newInstance(1)
                        }
                        return MainFragment.newInstance(position + 1)
                    }

                    override fun getCount(): Int {
                        return 3
                    }

                }

        listener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position != 1) {
                    blurImageView.alpha = max(0f, 1 - abs(positionOffset))
                } else {
                    blurImageView.alpha = max(0f, abs(positionOffset))
                }
            }

            override fun onPageSelected(position: Int) {}
        }

        viewPager.addOnPageChangeListener(listener)

        viewPager.currentItem = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 16) {
            initView()
        }
        if (requestCode == 32 && resultCode == RESULT_OK) {
            AfterImportTipFragment.newInstance().show(supportFragmentManager, "AfterImportTipFragment")
        }
        if (requestCode == 31 && resultCode == RESULT_OK) {
            initView()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (viewPager.currentItem != 1) {
            viewPager.currentItem = 1
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        AppWidgetUtils.updateWidget(applicationContext)
        viewPager.removeOnPageChangeListener(listener)
        super.onDestroy()
    }

}




