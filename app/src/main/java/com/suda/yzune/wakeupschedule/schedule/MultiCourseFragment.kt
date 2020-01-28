package com.suda.yzune.wakeupschedule.schedule

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_multi_course.*
import splitties.dimensions.dip

class MultiCourseFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_multi_course

    private var week = 0
    private var day = 0
    private var startNode = 0
    private val viewModel by activityViewModels<ScheduleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            week = it.getInt("week")
            day = it.getInt("day")
            startNode = it.getInt("startNode")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.fragment_multi_course, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewpager.pageMargin = context!!.dip(32)
        viewpager.offscreenPageLimit = 3
        viewpager.adapter = MultiCourseAdapter(childFragmentManager, viewModel.getMultiCourse(week, day, startNode))
    }

    companion object {
        @JvmStatic
        fun newInstance(weekParam: Int, dayParam: Int, startNodeParam: Int) = MultiCourseFragment().apply {
            arguments = Bundle().apply {
                putInt("week", weekParam)
                putInt("day", dayParam)
                putInt("startNode", startNodeParam)
            }
        }
    }
}
