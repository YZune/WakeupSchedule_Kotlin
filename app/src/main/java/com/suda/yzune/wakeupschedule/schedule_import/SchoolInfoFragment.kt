package com.suda.yzune.wakeupschedule.schedule_import


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_school_info.*

class SchoolInfoFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_school_info, container, false)
    }

    override fun onResume() {
        super.onResume()
        initEvent()
    }

    private fun initEvent() {
        ib_close.setOnClickListener {
            dismiss()
        }

//        tv_next.setOnClickListener {
//            if (et_school.text.toString() != "") {
//                val viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
//                viewModel.getSchoolInfo()[0] = et_school.text.toString()
//                viewModel.getSchoolInfo()[1] = et_type.text.toString()
//                val fragment = WebViewLoginFragment.newInstance("apply")
//                val transaction = activity!!.supportFragmentManager
//                transaction.add(R.id.fl_fragment, fragment, "webLogin")
//                transaction.commit()
//                dismiss()
//            } else {
//                Toasty.error()
//            }
//        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                SchoolInfoFragment().apply {

                }
    }
}
