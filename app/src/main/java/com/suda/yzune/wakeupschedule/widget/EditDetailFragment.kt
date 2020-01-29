package com.suda.yzune.wakeupschedule.widget

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.BaseDialogFragment
import com.google.android.material.chip.Chip
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_edit_detail.*

class EditDetailFragment : BaseDialogFragment() {

    private val detailData: ArrayList<String>? by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getStringArrayList("data")
    }

    private val title: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("title") ?: "编辑"
    }

    private val value: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("value") ?: ""
    }

    var listener: OnSaveClickedListener? = null

    override val layoutId: Int
        get() = R.layout.fragment_edit_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = title
        et_detail.setText(value)
        val hasData = detailData?.any {
            it.isNotBlank()
        } ?: false
        if (!hasData) {
            sv_details.visibility = View.GONE
            et_detail.hint = "请输入…"
        }
        detailData?.forEach {
            if (it.isEmpty()) return@forEach
            val chip = layoutInflater.inflate(R.layout.chip_group_item_choice, cg_details, false) as Chip
            chip.text = it
            cg_details.addView(chip)
        }
        cg_details.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId < 0) return@setOnCheckedChangeListener
            val t = group.findViewById<Chip>(checkedId).text
            et_detail.setText(t)
        }
        tv_save.setOnClickListener {
            listener?.save(et_detail, dialog!!)
        }
    }

    interface OnSaveClickedListener {
        fun save(editText: AppCompatEditText, dialog: Dialog)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String, data: ArrayList<String>, str: String) =
                EditDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("title", title)
                        putStringArrayList("data", data)
                        putString("value", str)
                    }
                }
    }
}
