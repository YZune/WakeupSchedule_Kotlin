package com.suda.yzune.wakeupschedule.schedule


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.suda.yzune.wakeupschedule.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_clipboard_import.*

class ClipboardImportFragment : DialogFragment() {

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_clipboard_import, container, false)
    }

    override fun onResume() {
        super.onResume()
        initEvent()
    }

    private fun initEvent() {
        ib_close.setOnClickListener {
            dismiss()
        }

        tv_mine.setOnClickListener {
            viewModel.tranClipboardList(false)
        }

        tv_lover.setOnClickListener {
            viewModel.tranClipboardList(true)
        }

        viewModel.getClipboardImportInfo().observe(this, Observer {
            when (it) {
                "解析异常" -> {
                    Toasty.error(activity!!.applicationContext, "数据解析异常，请确保完整复制").show()
                    dismiss()
                }
                "插入异常" -> {
                    Toasty.error(activity!!.applicationContext, "数据插入异常").show()
                    dismiss()
                }
                "love" -> {
                    Toasty.custom(activity!!.applicationContext, "导入成功（づ￣3￣）づ╭❤～祝你们长长久久", null,
                            activity!!.applicationContext.resources.getColor(R.color.colorAccent), false).show()
                    dismiss()
                }
                "ok" -> {
                    Toasty.success(activity!!.applicationContext, "导入成功ヽ(^o^)丿").show()
                    dismiss()
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                ClipboardImportFragment().apply {

                }
    }
}
