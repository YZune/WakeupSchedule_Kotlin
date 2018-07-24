package com.suda.yzune.wakeupschedule.schedule

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suda.yzune.wakeupschedule.R

class ScheduleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScheduleFragment()
    }
}
