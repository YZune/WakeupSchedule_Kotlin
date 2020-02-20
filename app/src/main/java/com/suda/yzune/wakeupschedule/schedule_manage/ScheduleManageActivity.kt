package com.suda.yzune.wakeupschedule.schedule_manage

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity
import com.suda.yzune.wakeupschedule.bean.TableSelectBean
import splitties.resources.color

class ScheduleManageActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_schedule_manage

    var subButton: AppCompatTextView? = null

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        tvButton.text = "清空"
        tvButton.typeface = Typeface.DEFAULT_BOLD
        tvButton.setTextColor(color(R.color.colorAccent))
        subButton = tvButton
        return tvButton
    }

    private val viewModel by viewModels<ScheduleManageViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setResult(RESULT_OK)
    }

    private fun initView() {
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            mainTitle.text = destination.label
            if (destination.id == R.id.courseManageFragment) {
                subButton?.visibility = View.VISIBLE
            } else {
                subButton?.visibility = View.INVISIBLE
            }
        }
        intent.extras?.getParcelable<TableSelectBean>("selectedTable")?.let {
            val bundle = Bundle()
            bundle.putParcelable("selectedTable", it)
            navController.navigate(R.id.scheduleManageFragment_to_courseManageFragment, bundle)
        }
    }
}
