package com.suda.yzune.wakeupschedule.course_add

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.BaseDialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.widget.SelectedRecyclerView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_select_week.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.textColor

class SelectWeekFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_select_week

    var position = -1
    private lateinit var viewModel: AddCourseViewModel
    private val liveData = MutableLiveData<ArrayList<Int>>()
    private val result = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
        viewModel = ViewModelProviders.of(activity!!).get(AddCourseViewModel::class.java)
        liveData.observe(this, Observer {
            if (it?.size == viewModel.maxWeek) {
                tv_all.setTextColor(Color.WHITE)
                tv_all.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (it?.size != viewModel.maxWeek) {
                tv_all.setTextColor(Color.BLACK)
                tv_all.background = null
            }
            val flag = viewModel.judgeType(it!!)
            if (flag == 1) {
                tv_type1.setTextColor(Color.WHITE)
                tv_type1.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (flag != 1) {
                tv_type1.setTextColor(Color.BLACK)
                tv_type1.background = null
            }
            if (flag == 2) {
                tv_type2.setTextColor(Color.WHITE)
                tv_type2.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (flag != 2) {
                tv_type2.setTextColor(Color.BLACK)
                tv_type2.background = null
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveData.value = viewModel.editList[position].weekList.value
        result.addAll(liveData.value!!)
        showWeeks()
        initEvent()
    }

    private fun showWeeks() {
        val adapter = SelectWeekAdapter(R.layout.item_select_week, viewModel.maxWeek, result)
        adapter.bindToRecyclerView(rv_week)
        rv_week.layoutManager = StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL)
        var prePos = -1
        rv_week.positionChangedListener = object : SelectedRecyclerView.PositionChangedListener {
            override fun changeState(pos: Int, isDown: Boolean) {
                if (prePos != pos || isDown) {
                    if (pos in 0 until viewModel.maxWeek) {
                        if (!result.contains(pos + 1)) {
                            result.add(pos + 1)
                            adapter.getViewByPosition(pos, R.id.tv_num)?.backgroundResource =
                                    R.drawable.week_selected_bg
                            (adapter.getViewByPosition(pos, R.id.tv_num) as TextView).textColor =
                                    Color.WHITE
                        } else {
                            result.remove(pos + 1)
                            adapter.getViewByPosition(pos, R.id.tv_num)?.background = null
                            (adapter.getViewByPosition(pos, R.id.tv_num) as TextView).textColor =
                                    Color.BLACK
                        }
                        liveData.value = result
                    }
                    if (prePos != pos) {
                        prePos = pos
                    }
                }
            }
        }
    }

    private fun initEvent() {
        tv_all.setOnClickListener {
            if (tv_all.background == null) {
                result.clear()
                for (i in 1..viewModel.maxWeek) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            } else {
                result.clear()
                showWeeks()
                liveData.value = result
            }
        }

        tv_type1.setOnClickListener {
            if (tv_type1.background == null) {
                result.clear()
                for (i in 1..viewModel.maxWeek step 2) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            }
        }

        tv_type2.setOnClickListener {
            if (tv_type2.background == null) {
                result.clear()
                for (i in 2..viewModel.maxWeek step 2) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            }
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_save.setOnClickListener {
            if (result.size == 0) {
                Toasty.error(context!!.applicationContext, "请至少选择一周").show()
            } else {
                viewModel.editList[position].weekList.value = result
                dismiss()
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(arg: Int) =
                SelectWeekFragment().apply {
                    arguments = Bundle().apply {
                        putInt("position", arg)
                    }
                }
    }
}
