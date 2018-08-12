package com.suda.yzune.wakeupschedule.schedule


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_donate.*
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import com.suda.yzune.wakeupschedule.DonateActivity
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.DonateUtils
import es.dmoral.toasty.Toasty


class DonateFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_donate, container, false)
    }

    override fun onResume() {
        super.onResume()
        initEvent()
    }

    private fun initEvent() {
        ib_close.setOnClickListener {
            dismiss()
        }

        tv_star.setOnClickListener {
            try {
                val uri = Uri.parse("market://details?id=com.suda.yzune.wakeupschedule")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity!!.startActivity(intent)
            } catch (e: Exception) {
                Toasty.info(context!!.applicationContext, "没有检测到应用商店o(╥﹏╥)o").show()
            }
        }

        tv_donate.setOnClickListener {
            if (DonateUtils.isAppInstalled(context!!.applicationContext, "com.eg.android.AlipayGphone")) {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val qrCodeUrl = Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=HTTPS://QR.ALIPAY.COM/FKX09148M0LN2VUUZENO9B?_s=web-other")
                intent.data = qrCodeUrl
                intent.setClassName("com.eg.android.AlipayGphone", "com.alipay.mobile.quinox.LauncherActivity")
                startActivity(intent)
                Toasty.success(context!!.applicationContext, "非常感谢(*^▽^*)").show()
            } else {
                Toasty.info(context!!.applicationContext, "没有检测到支付宝客户端o(╥﹏╥)o").show()
            }
        }

        tv_feedback.setOnClickListener {
            if (CourseUtils.isQQClientAvailable(context!!.applicationContext)) {
                val qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=1055614742&version=1"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)))
            } else {
                Toasty.info(context!!.applicationContext, "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show()
            }
        }

        tv_donate_list.setOnClickListener {
            startActivity(Intent(activity, DonateActivity::class.java))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                DonateFragment().apply {

                }
    }
}
