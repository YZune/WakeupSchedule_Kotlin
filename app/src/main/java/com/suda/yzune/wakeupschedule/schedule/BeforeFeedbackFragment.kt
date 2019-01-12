package com.suda.yzune.wakeupschedule.schedule


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.intro.IntroActivity
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_before_feedback.*
import org.jetbrains.anko.startActivity
import java.util.*

class BeforeFeedbackFragment : androidx.fragment.app.DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_before_feedback, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window?.setLayout((dm.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        ib_close.setOnClickListener {
            dismiss()
        }

        tv_guide.setOnClickListener {
            activity!!.startActivity<IntroActivity>()
        }

        tv_feedback.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            if (hour !in 8..21) {
                Toasty.info(activity!!.applicationContext, "开发者在休息哦(～﹃～)~zZ请换个时间反馈吧").show()
            } else {
                if (CourseUtils.isQQClientAvailable(activity!!.applicationContext)) {
                    val qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=1055614742&version=1"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)))
                } else {
                    Toasty.error(activity!!.applicationContext, "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BeforeFeedbackFragment()
    }
}
