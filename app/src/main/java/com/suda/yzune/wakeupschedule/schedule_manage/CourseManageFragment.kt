package com.suda.yzune.wakeupschedule.schedule_manage


import android.appwidget.AppWidgetManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.course_add.AddCourseActivity
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.support.v4.startActivity

class CourseManageFragment : Fragment() {

    private lateinit var viewModel: ScheduleManageViewModel
    private var tablePosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleManageViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_course_manage, container, false)
        val rvCourseList = view.findViewById<RecyclerView>(R.id.rv_course_list)
        tablePosition = arguments!!.getInt("position")
        viewModel.getCourseBaseBeanListByTable(viewModel.tableSelectList[tablePosition].id).observe(this, Observer {
            if (it == null) return@Observer
            viewModel.courseList.clear()
            viewModel.courseList.addAll(it)
            if (rvCourseList.adapter == null) {
                initRecyclerView(rvCourseList, viewModel.courseList)
            } else {
                rvCourseList!!.adapter!!.notifyDataSetChanged()
            }
        })
        return view
    }

    private fun initRecyclerView(rvCourseList: RecyclerView, data: List<CourseBaseBean>) {
        rvCourseList.layoutManager = LinearLayoutManager(context)
        val adapter = CourseListAdapter(R.layout.item_course_list, data)
        adapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_edit -> {
                    startActivity<AddCourseActivity>(
                            "id" to data[position].id,
                            "tableId" to data[position].tableId,
                            "maxWeek" to viewModel.tableSelectList[tablePosition].maxWeek,
                            "nodes" to viewModel.tableSelectList[tablePosition].nodes
                    )
                }
                R.id.ib_delete -> {
                    Toasty.info(activity!!.applicationContext, "长按删除课程哦~").show()
                }
            }
        }
        adapter.setOnItemChildLongClickListener { _, view, position ->
            when (view.id) {
                R.id.ib_delete -> {
                    viewModel.deleteCourse(data[position])
                    val appWidgetManager = AppWidgetManager.getInstance(activity!!.applicationContext)
                    viewModel.getScheduleWidgetIds().observe(this, Observer { list ->
                        list?.forEach {
                            when (it.detailType) {
                                0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                                1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                            }
                        }
                    })
                    return@setOnItemChildLongClickListener true
                }
                else -> {
                    return@setOnItemChildLongClickListener false
                }
            }

        }
        adapter.addFooterView(initFooterView())
        rvCourseList.adapter = adapter
    }

    private fun initFooterView(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_add_course_btn, null)
        val tvBtn = view.findViewById<TextView>(R.id.tv_add)
        tvBtn.text = "添加"
        tvBtn.setOnClickListener {
            startActivity<AddCourseActivity>(
                    "id" to -1,
                    "tableId" to viewModel.tableSelectList[tablePosition].id,
                    "maxWeek" to viewModel.tableSelectList[tablePosition].maxWeek,
                    "nodes" to viewModel.tableSelectList[tablePosition].nodes
            )
        }
        return view
    }

}
