package android.support.v4.app


import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.android.synthetic.main.fragment_after_import_tip.*

class AfterImportTipFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return inflater.inflate(R.layout.fragment_after_import_tip, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window?.setLayout((dm.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_content.text = ViewUtils.getHtmlSpannedString("记得<b><font color='#fa6278'>仔细检查</font></b>有没有少课、课程信息对不对哦<br>不要到时候<b><font color='#fa6278'>一不小心就翘课</font></b>啦<br>解析算法不是100%可靠的哦<br>但会朝这个方向努力")
        ib_close.setOnClickListener {
            dismiss()
        }
        tv_know.setOnClickListener {
            dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String) {
        mDismissed = false
        mShownByMe = true
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AfterImportTipFragment()
    }
}
