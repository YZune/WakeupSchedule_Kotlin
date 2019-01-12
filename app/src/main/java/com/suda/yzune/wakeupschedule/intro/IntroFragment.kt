package com.suda.yzune.wakeupschedule.intro


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.suda.yzune.wakeupschedule.GlideApp
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils

class IntroFragment : androidx.fragment.app.Fragment() {

    private var imageUrl = ""
    private var description = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString("imageUrl")!!
            description = it.getString("description")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_intro, container, false)
        val x = (ViewUtils.getRealSize(activity!!).x * 0.3).toInt()
        val y = (ViewUtils.getRealSize(activity!!).y * 0.3).toInt()
        GlideApp.with(this)
                .load(imageUrl)
                .override(x, y)
                .error(R.drawable.net_work_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view.findViewById(R.id.iv_intro))
        view.findViewById<TextView>(R.id.tv_description).text = description
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                IntroFragment().apply {
                    arguments = Bundle().apply {
                        putString("imageUrl", param1)
                        putString("description", param2)
                    }
                }
    }
}
