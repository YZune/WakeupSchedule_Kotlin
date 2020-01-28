package com.suda.yzune.wakeupschedule.schedule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.BaseDialogFragment
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_before_feedback.*
import java.util.*

class BeforeFeedbackFragment : BaseDialogFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_before_feedback

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ib_close.setOnClickListener {
            dismiss()
        }

        tv_guide.setOnClickListener {
            dismiss()
            if (activity is ScheduleActivity) {
                (activity as ScheduleActivity).initIntro()
            }
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
