package com.suda.yzune.wakeupschedule.apply_info

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_apply_info.*

class ApplyInfoActivity : AppCompatActivity() {

    private lateinit var viewModel: ApplyInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_info)
        ViewUtils.resizeStatusBar(this, v_status)
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

        ib_back.setOnClickListener {
            finish()
        }
    }

    private fun initHeaderView(): View {
        return LayoutInflater.from(this).inflate(R.layout.item_apply_info_header, null)
    }
}
