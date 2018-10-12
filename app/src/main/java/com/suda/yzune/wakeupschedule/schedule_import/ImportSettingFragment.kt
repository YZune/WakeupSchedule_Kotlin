package com.suda.yzune.wakeupschedule.schedule_import


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_import_setting.*

class ImportSettingFragment : DialogFragment() {

    private lateinit var viewModel: ImportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_import_setting, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window.setLayout((dm.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        tv_cover.setOnClickListener {
            viewModel.importId = activity!!.intent.extras.getInt("tableId")
            dismiss()
        }

        tv_new.setOnClickListener {
            viewModel.importId = viewModel.newId
            dismiss()
        }

        tv_cancel.setOnClickListener {
            dismiss()
            activity!!.finish()
        }
    }
}
