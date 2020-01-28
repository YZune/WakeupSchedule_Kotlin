package com.suda.yzune.wakeupschedule.suda_life

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_bath.*

class BathFragment : BaseFragment() {

    private lateinit var viewModel: SudaLifeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(SudaLifeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bath, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshData()
        cv_female.setOnClickListener {
            refreshData(true)
        }
        cv_male.setOnClickListener {
            refreshData(true)
        }
    }

    private fun refreshData(refresh: Boolean = false) {
        launch {
            try {
                viewModel.getBathData(true)
                val count = if (viewModel.maleBathData.inNum > viewModel.maleBathData.outNum) {
                    viewModel.maleBathData.inNum - viewModel.maleBathData.outNum
                } else {
                    0
                }
                tv_male_stay.text = count.toString()
                tv_male_rate.text = "拥挤度：${(count / 80f) * 100}%"
                if (refresh) {
                    Toasty.success(activity!!, "刷新成功").show()
                }
            } catch (e: Exception) {
                Toasty.error(activity!!, "发生异常>_<${e.message}").show()
            }
        }

        launch {
            try {
                viewModel.getBathData(false)
                val count = if (viewModel.femaleBathData.inNum > viewModel.femaleBathData.outNum) {
                    viewModel.femaleBathData.inNum - viewModel.femaleBathData.outNum
                } else {
                    0
                }
                tv_female_stay.text = count.toString()
                tv_female_rate.text = "拥挤度：${(count / 90f) * 100}%"
            } catch (e: Exception) {
                Toasty.error(activity!!, "发生异常>_<${e.message}").show()
            }
        }
    }
}
