package com.suda.yzune.wakeupschedule.apply_info

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_apply_info.*

class ApplyInfoActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_apply_info

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        return null
    }

    private lateinit var viewModel: ApplyInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ApplyInfoViewModel::class.java)
        rv_info.adapter = ApplyInfoAdapter(R.layout.item_apply_info, viewModel.countList).apply {
            this.setHeaderView(initHeaderView())
        }
        rv_info.layoutManager = LinearLayoutManager(this)
        viewModel.initData()
        srl_info.setColorSchemeColors(ContextCompat.getColor(applicationContext, R.color.colorAccent))
        srl_info.isRefreshing = true
        viewModel.countInfo.observe(this, Observer {
            when (it) {
                "OK" -> {
                    rv_info.adapter?.notifyDataSetChanged()
                    srl_info.isRefreshing = false
                }
                "error" -> {
                    Toasty.error(applicationContext, "网络错误").show()
                    srl_info.isRefreshing = false
                }
            }
        })

        srl_info.setOnRefreshListener {
            viewModel.initData()
        }
    }

    private fun initHeaderView(): View {
        return LayoutInflater.from(this).inflate(R.layout.item_apply_info_header, null)
    }
}
