package com.suda.yzune.wakeupschedule.apply_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColorResource
import org.jetbrains.anko.topPadding

class ApplyInfoActivity : BaseListActivity() {

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        tvButton.text = "刷新"
        tvButton.textColorResource = R.color.colorAccent
        tvButton.setOnClickListener {
            viewModel.initData()
        }
        return tvButton
    }

    private lateinit var viewModel: ApplyInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ApplyInfoViewModel::class.java)
        mRecyclerView.adapter = ApplyInfoAdapter(R.layout.item_apply_info, viewModel.countList).apply {
            this.setHeaderView(initHeaderView())
        }
        mRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewModel.initData()
        viewModel.countInfo.observe(this, Observer {
            when (it) {
                "OK" -> {
                    mRecyclerView.adapter?.notifyDataSetChanged()
                }
                "error" -> {
                    Toasty.error(applicationContext, "网络错误").show()
                }
            }
        })

    }

    private fun initHeaderView(): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_apply_info_header, null)
        view.topPadding = getStatusBarHeight() + dip(48)
        return view
    }
}
