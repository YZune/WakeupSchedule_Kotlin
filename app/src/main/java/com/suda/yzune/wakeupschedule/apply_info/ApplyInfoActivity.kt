package com.suda.yzune.wakeupschedule.apply_info

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.schedule_import.LoginWebActivity
import com.suda.yzune.wakeupschedule.widget.snackbar.action
import com.suda.yzune.wakeupschedule.widget.snackbar.longSnack
import es.dmoral.toasty.Toasty
import splitties.activities.start
import splitties.dimensions.dip
import splitties.views.topPadding

class ApplyInfoActivity : BaseListActivity() {

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        val iconFont = ResourcesCompat.getFont(this, R.font.iconfont)
        tvButton.typeface = iconFont
        tvButton.textSize = 20f
        tvButton.text = "\uE6D7"
        tvButton.setOnClickListener {
            viewModel.initData()
        }
        return tvButton
    }

    private lateinit var viewModel: ApplyInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        showSearch = true
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
                mRecyclerView.adapter?.notifyDataSetChanged()
                if (viewModel.filterList.isEmpty()) {
                    mRecyclerView.longSnack("没有找到你的学校哦") {
                        action("申请适配") {
                            start<LoginWebActivity> {
                                putExtra("import_type", "apply")
                            }
                        }
                    }
                }
            }

        }
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ApplyInfoViewModel::class.java)
        mRecyclerView.adapter = ApplyInfoAdapter(R.layout.item_apply_info, viewModel.filterList).apply {
            this.setHeaderView(initHeaderView())
        }
        mRecyclerView.layoutManager = LinearLayoutManager(this)
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
