package com.suda.yzune.wakeupschedule.schedule


import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.schedule_import.SchoolListActivity
import kotlinx.android.synthetic.main.fragment_import_choose.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class ImportChooseFragment : DialogFragment() {

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_import_choose, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window?.setLayout((dm.widthPixels * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        initEvent()
    }

    private fun initEvent() {
        ib_close.setOnClickListener {
            dismiss()
        }

        tv_file.setOnClickListener {
            if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                dismiss()
            } else {
                activity!!.startActivityForResult<LoginWebActivity>(32, "type" to "file")
                dismiss()
            }
        }

        tv_suda.setOnClickListener {
            activity!!.startActivityForResult<LoginWebActivity>(
                    32,
                    "type" to "苏州大学",
                    "tableId" to viewModel.table.id
            )
            dismiss()
        }

        tv_more.setOnClickListener {
            activity!!.startActivityForResult<SchoolListActivity>(32)
            dismiss()
        }

        tv_feedback.setOnClickListener {
            activity!!.startActivity<LoginWebActivity>("type" to "apply")
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ImportChooseFragment()
    }
}
