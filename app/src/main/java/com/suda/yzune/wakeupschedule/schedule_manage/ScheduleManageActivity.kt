package com.suda.yzune.wakeupschedule.schedule_manage

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity

class ScheduleManageActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_schedule_manage

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        return null
    }

    private lateinit var viewModel: ScheduleManageViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(ScheduleManageViewModel::class.java)
        super.onCreate(savedInstanceState)

        initView()
        setResult(RESULT_OK)
    }

    private fun initView() {
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        navController.addOnNavigatedListener { _, destination ->
            mainTitle.text = destination.label
        }
    }
}
