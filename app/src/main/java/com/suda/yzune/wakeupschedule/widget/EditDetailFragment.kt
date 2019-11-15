package com.suda.yzune.wakeupschedule.widget


import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import com.google.android.material.chip.Chip
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_edit_detail.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.longToast

class EditDetailFragment : BaseDialogFragment() {

    private val detailData: ArrayList<String>? by lazy {
        arguments?.getStringArrayList("data")
    }

    override val layoutId: Int
        get() = R.layout.fragment_edit_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hasData = detailData?.any {
            it.isNotBlank()
        } ?: false
        if (!hasData) {
            cg_details.visibility = View.GONE
        }
        detailData?.forEach {
            if (it.isEmpty()) return@forEach
            val chip = layoutInflater.inflate(R.layout.chip_group_item_choice, cg_details, false) as Chip
            chip.text = it
            cg_details.addView(chip)
        }

        cg_details.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId < 0) return@setOnCheckedChangeListener
            val t = group.find<Chip>(checkedId).text
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(data: ArrayList<String>) =
                EditDetailFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList("data", data)
                    }
                }
    }
}
