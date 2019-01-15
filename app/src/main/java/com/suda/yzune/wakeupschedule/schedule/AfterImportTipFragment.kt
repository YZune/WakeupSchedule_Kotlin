package com.suda.yzune.wakeupschedule.schedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_after_import_tip.*

class AfterImportTipFragment : BaseDialogFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_after_import_tip

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        tv_content.text = ViewUtils.getHtmlSpannedString("记得<b><font color='#fa6278'>仔细检查</font></b>有没有少课、课程信息对不对哦<br>不要到时候<b><font color='#fa6278'>一不小心就翘课</font></b>啦<br>解析算法不是100%可靠的哦<br>但会朝这个方向努力")
        ib_close.setOnClickListener {
            dismiss()
        }
        tv_know.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AfterImportTipFragment()
    }
}
