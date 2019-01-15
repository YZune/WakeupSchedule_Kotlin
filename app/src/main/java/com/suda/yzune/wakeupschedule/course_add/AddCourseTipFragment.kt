package com.suda.yzune.wakeupschedule.course_add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.BaseDialogFragment
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_add_course_tip.*

class AddCourseTipFragment : BaseDialogFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_add_course_tip

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
