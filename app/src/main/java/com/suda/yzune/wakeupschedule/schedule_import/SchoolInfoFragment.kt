package com.suda.yzune.wakeupschedule.schedule_import

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_school_info.*

class SchoolInfoFragment : BaseFragment() {

    private lateinit var viewModel: ImportViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_school_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
        ViewUtils.resizeStatusBar(context!!.applicationContext, v_status)
        initEvent()
    }

    private fun initEvent() {
        ib_back.setOnClickListener {
            activity!!.finish()
        }

        chip_urp.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isUrp = isChecked
        }

        tv_next.setOnClickListener {
            if (et_school.text.toString() != "") {
                viewModel.schoolInfo[0] = et_school.text.toString()
                viewModel.schoolInfo[1] = et_type.text.toString()
                viewModel.schoolInfo[2] = et_qq.text.toString()
                val fragment = WebViewLoginFragment.newInstance("apply")
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                transaction.add(R.id.fl_fragment, fragment, "webLogin")
                transaction.commit()
            } else {
                Toasty.error(activity!!.applicationContext, "请填写学校全称").show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SchoolInfoFragment()
    }
}
