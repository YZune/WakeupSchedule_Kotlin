package com.suda.yzune.wakeupschedule


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.suda.yzune.wakeupschedule.bean.UpdateInfoBean
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_update.*

class UpdateFragment : DialogFragment() {

    private lateinit var updateInfo: UpdateInfoBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            updateInfo = it.getParcelable("updateInfo")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_update, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window?.setLayout((dm.widthPixels * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onResume() {
        super.onResume()
        tv_old_version.text = "当前版本：" + UpdateUtils.getVersionName(context!!.applicationContext)
        tv_new_version.text = "最新版本：" + updateInfo.VersionName
        tv_info.text = updateInfo.VersionInfo
        tv_visit.setOnClickListener {
            if (BuildConfig.CHANNEL == "google") {
                try {
                    val uri = Uri.parse("market://details?id=com.suda.yzune.wakeupschedule.pro")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity!!.startActivity(intent)
                } catch (e: Exception) {
                    Toasty.info(context!!.applicationContext, "没有检测到应用商店o(╥﹏╥)o").show()
                }
            } else {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                val contentUrl = Uri.parse("https://www.coolapk.com/apk/com.suda.yzune.wakeupschedule")
                intent.data = contentUrl
                context!!.startActivity(intent)
            }
            dismiss()
        }
        ib_close.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arg: UpdateInfoBean) =
                UpdateFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("updateInfo", arg)
                    }
                }
    }
}
