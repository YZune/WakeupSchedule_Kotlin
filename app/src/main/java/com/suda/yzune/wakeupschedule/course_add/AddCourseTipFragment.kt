package com.suda.yzune.wakeupschedule.course_add


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_add_course_tip.*

class AddCourseTipFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_add_course_tip, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window?.setLayout((dm.widthPixels * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        GlideApp.with(this)
                .load("https://ws2.sinaimg.cn/large/006tNbRwgy1fwqehetvi3j30u00lc40a.jpg")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv_tip)

        ib_close.setOnClickListener {
            dismiss()
        }

        tv_know.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddCourseTipFragment()
    }
}
