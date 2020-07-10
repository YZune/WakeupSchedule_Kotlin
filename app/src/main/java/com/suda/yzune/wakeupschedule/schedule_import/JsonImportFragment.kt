package com.suda.yzune.wakeupschedule.schedule_import

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_json_import.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JsonImportFragment : BaseFragment() {
    private val viewModel by activityViewModels<ImportViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_json_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, v_status)


        tv_import.setOnClickListener {
            startImport()
        }

        ib_back.setOnClickListener {
            activity!!.finish()
        }
    }

    private fun startImport() {
        launch {
            val import = withContext(Dispatchers.IO) {
                try {
                    viewModel.importFromJson(et_editjson.text.toString())
                    null
                } catch (e: Exception) {
                    e.printStackTrace()
                    e.message
                }
            }
            if (import != null) {
                Toasty.error(activity!!, "发生异常>_<请确保JSON格式正确\n${import}", Toast.LENGTH_LONG).show()
            } else {
                Toasty.success(activity!!, "导入成功(ﾟ▽ﾟ)/", Toast.LENGTH_LONG).show()
                activity!!.finish()
            }

        }
    }
}