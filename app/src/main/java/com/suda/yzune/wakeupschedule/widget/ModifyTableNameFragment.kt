package com.suda.yzune.wakeupschedule.widget

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_modify_table_name.*

class ModifyTableNameFragment : androidx.fragment.app.DialogFragment() {

    private var listener: TableNameChangeListener? = null
    private var tableName = ""
    private var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listener = it.getParcelable("listener")
            tableName = it.getString("tableName")!!
            title = it.getString("title")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_modify_table_name, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window?.setLayout((dm.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        tv_title.text = title
        et_table_name.setText(tableName)
        et_table_name.setSelection(tableName.length)
        initEvent()
    }

    private fun initEvent() {
        tv_save.setOnClickListener {
            listener?.onFinish(et_table_name, dialog)
        }

        tv_cancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface TableNameChangeListener : Parcelable {
        fun onFinish(editText: EditText, dialog: Dialog)
    }

    companion object {
        @JvmStatic
        fun newInstance(changeListener: TableNameChangeListener, string: String = "", titleStr: String = "课表名字") =
                ModifyTableNameFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("listener", changeListener)
                        putString("tableName", string)
                        putString("title", titleStr)
                    }
                }
    }
}
