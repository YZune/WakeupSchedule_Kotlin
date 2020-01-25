package com.suda.yzune.wakeupschedule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import com.suda.yzune.wakeupschedule.bean.UpdateInfoBean
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_update.*

class UpdateFragment : BaseDialogFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_update

    private lateinit var updateInfo: UpdateInfoBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            updateInfo = it.getParcelable("updateInfo")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
