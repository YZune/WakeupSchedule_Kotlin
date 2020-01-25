package com.suda.yzune.wakeupschedule.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.apply_info.ApplyInfoActivity
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.intro.AboutActivity
import com.suda.yzune.wakeupschedule.intro.IntroYoungActivity
import com.suda.yzune.wakeupschedule.schedule.BeforeFeedbackFragment
import com.suda.yzune.wakeupschedule.schedule.ScheduleViewModel
import com.suda.yzune.wakeupschedule.settings.SettingsActivity
import kotlinx.android.synthetic.main.fragment_dash_board.*
import splitties.fragments.start

class DashBoardFragment : BaseFragment() {

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v_status.layoutParams.height = viewModel.statusBarMargin

        initEvent()
    }

    private fun initEvent() {
        ll_settings.setOnClickListener {
            activity!!.startActivityForResult(
                    Intent(activity, SettingsActivity::class.java), 31)
        }

        ll_about.setOnClickListener {
            start<AboutActivity>()
        }

        ll_apply.setOnClickListener {
            start<ApplyInfoActivity>()
        }

        ll_young.setOnClickListener {
            start<IntroYoungActivity>()
        }

        ll_feedback.setOnClickListener {
            BeforeFeedbackFragment.newInstance().apply {
                isCancelable = false
            }.show(fragmentManager!!, "BeforeFeedbackFragment")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                DashBoardFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}
