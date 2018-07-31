package com.suda.yzune.wakeupschedule.course_add

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.util.Log
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule.ScheduleViewModel
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_add_course.*
import com.suda.yzune.wakeupschedule.MainActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import es.dmoral.toasty.Toasty


class AddCourseActivity : AppCompatActivity(), AddCourseAdapter.OnItemEditTextChangedListener {

    private lateinit var viewModel: AddCourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        ViewUtils.resizeStatusBar(this, v_status)

        viewModel = ViewModelProviders.of(this).get(AddCourseViewModel::class.java)
        viewModel.initRepository(applicationContext)

        viewModel.getLastId().observe(this, Observer {
            if (it != null) {
                viewModel.newId = it + 1
            } else {
                viewModel.newId = 0
            }
        })

        initAdapter()
    }

    override fun onEditTextAfterTextChanged(editable: Editable, position: Int, what: String) {
        when (what){
            "room" -> viewModel.getList()[position].room = editable.toString()
            "teacher" -> viewModel.getList()[position].teacher = editable.toString()
        }
    }

    private fun initAdapter() {
        val adapter = AddCourseAdapter(R.layout.item_add_course_detail, viewModel.initData(intent.extras.getInt("type")))
        adapter.setListener(this)
        adapter.addHeaderView(initHeaderView(adapter))
        adapter.addFooterView(initFooterView(adapter))
        adapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_delete -> {
                    if (adapter.data.size == 1) {
                        Toasty.error(this, "至少要保留一个时间段").show()
                    } else {
                        adapter.remove(position)
                        // 这里的position就是数据在List中的下标
//                        for (i in (position + 1)..(adapter.itemCount - 2)) {
//                            (adapter.getViewByPosition(rv_detail, i, R.id.tv_item_id) as TextView).text = "$i"
//                        }
                    }
                }
                R.id.ll_weeks -> {
                    viewModel.initWeekArrayList(position)
                    val selectWeekDialog = SelectWeekFragment.newInstance(position)
                    selectWeekDialog.show(supportFragmentManager, "selectWeek")
                }
            }
        }
        rv_detail.adapter = adapter
        rv_detail.layoutManager = LinearLayoutManager(this)
    }

    private fun initHeaderView(adapter: AddCourseAdapter): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_add_course_base, null)
        return view
    }

    private fun initFooterView(adapter: AddCourseAdapter): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_add_course_btn, null)
        val cvBtn = view.findViewById<CardView>(R.id.cv_add)
        cvBtn.setOnClickListener {
            adapter.addData(viewModel.newBlankCourse())
        }
        return view
    }
}
