package com.suda.yzune.wakeupschedule.schedule_import

import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.activityViewModels
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_import_setting.*

class ImportSettingFragment : BaseDialogFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_import_setting

    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_cover.setOnClickListener {
            viewModel.importId = activity!!.intent.extras!!.getInt("tableId", -1)
            viewModel.newFlag = false
            dismiss()
        }

        tv_new.setOnClickListener {
            launch {
                viewModel.importId = viewModel.getNewId()
                viewModel.newFlag = true
                dismiss()
            }
        }

        tv_cancel.setOnClickListener {
            dismiss()
            activity!!.finish()
        }
    }
}
