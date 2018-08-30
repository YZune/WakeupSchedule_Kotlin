package com.suda.yzune.wakeupschedule.schedule


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_import_choose.*

class ImportChooseFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_import_choose, container, false)
    }

    override fun onResume() {
        super.onResume()
        initEvent()
    }

    private fun initEvent() {
        ib_close.setOnClickListener {
            dismiss()
        }

        tv_suda.setOnClickListener {
            val intent = Intent(activity!!, LoginWebActivity::class.java)
            intent.putExtra("type", "suda")
            startActivity(intent)
            dismiss()
        }

        tv_fangzheng.setOnClickListener {
            val intent = Intent(activity!!, LoginWebActivity::class.java)
            intent.putExtra("type", "FZ")
            startActivity(intent)
            dismiss()
        }

        tv_new_fangzheng.setOnClickListener {
            val intent = Intent(activity!!, LoginWebActivity::class.java)
            intent.putExtra("type", "newFZ")
            startActivity(intent)
            dismiss()
        }

        tv_feedback.setOnClickListener {
            if (CourseUtils.isQQClientAvailable(context!!.applicationContext)) {
                val qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=1055614742&version=1"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)))
            } else {
                Toasty.info(context!!.applicationContext, "手机上没有安装QQ，无法启动聊天窗口:-(", Toast.LENGTH_LONG).show()
            }
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                ImportChooseFragment().apply {

                }
    }
}
