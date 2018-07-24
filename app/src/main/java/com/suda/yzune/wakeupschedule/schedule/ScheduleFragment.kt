package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suda.yzune.wakeupschedule.R

class ScheduleFragment : Fragment() {

    var week = 0
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        refresh()
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: Int) =
                ScheduleFragment().apply {
                    week = arg
                }
    }

    fun refresh(){
        for (i in 1..7){
            viewModel.getRawCourseByDay(i).observe(this, Observer {
                if (it != null){
                    viewModel.getCourseByDay(it).observe(this, Observer {
                        it?.forEach {
                            Log.d("课程", it.toString())
                        }
                    })
                }
            })
        }
    }
}
