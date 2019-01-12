package androidx.fragment.app

import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.schedule.ScheduleViewModel
import es.dmoral.toasty.Toasty
import gdut.bsx.share2.FileUtil
import gdut.bsx.share2.Share2
import gdut.bsx.share2.ShareContentType
import kotlinx.android.synthetic.main.fragment_export_settings.*
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.CoroutineContext

class ExportSettingsFragment : DialogFragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        viewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return inflater.inflate(R.layout.fragment_export_settings, container, false)
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog.window?.setLayout((dm.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        tv_export.setOnClickListener {
            launch {
                val task = async(Dispatchers.IO) {
                    try {
                        viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
                        "ok"
                    } catch (e: Exception) {
                        e.message
                    }
                }.await()
                if (task == "ok") {
                    Toasty.success(activity!!.applicationContext, "导出成功").show()
                    dismiss()
                } else {
                    Toasty.error(activity!!.applicationContext, "出现异常>_<\n$task").show()
                }
            }
        }

        tv_share.setOnClickListener {
            launch {
                val task = async(Dispatchers.IO) {
                    try {
                        viewModel.exportData(Environment.getExternalStorageDirectory().absolutePath)
                    } catch (e: Exception) {
                        null
                    }
                }.await()
                if (task != null) {
                    Share2.Builder(activity)
                            .setContentType(ShareContentType.FILE)
                            .setShareFileUri(FileUtil.getFileUri(activity, null, File(task)))
                            .setTitle("导出并分享课程文件")
                            .build()
                            .shareBySystem()
                    dismiss()
                } else {
                    Toasty.error(activity!!.applicationContext, "出现异常>_<").show()
                }
            }
        }

        tv_cancel.setOnClickListener {
            dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String) {
        mDismissed = false
        mShownByMe = true
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    override fun dismiss() {
        super.dismiss()
        job.cancel()
    }
}
