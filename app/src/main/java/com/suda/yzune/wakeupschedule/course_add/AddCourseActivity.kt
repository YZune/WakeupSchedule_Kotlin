package com.suda.yzune.wakeupschedule.course_add

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule.ScheduleViewModel
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_add_course.*

class AddCourseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        ViewUtils.resizeStatusBar(this, v_status)

        val viewModel = ViewModelProviders.of(this).get(AddCourseViewModel::class.java)
        viewModel.initRepository(applicationContext)

        viewModel.getLastId().observe(this, Observer {
            if (it != null) {
                viewModel.newId = it + 1
            } else {
                viewModel.newId = 0
            }
        })

        initAdapter(viewModel)
    }

    private fun initAdapter(viewModel: AddCourseViewModel){
        val adapter = AddCourseAdapter(R.layout.item_add_course_detail, viewModel.initData(intent.extras.getInt("type")))
        //adapter.addFooterView()
        rv_detail.adapter = adapter
        rv_detail.layoutManager = LinearLayoutManager(this)
    }
}
