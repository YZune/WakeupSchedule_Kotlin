package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Activity.RESULT_OK
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.apply_info.ApplyInfoActivity
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.widget.snackbar.longSnack
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_web_view_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import splitties.activities.start

class WebViewLoginFragment : BaseFragment() {

    private lateinit var url: String
    private lateinit var viewModel: ImportViewModel
    private var isRefer = false
    private val hostRegex = Regex("""(http|https)://.*?/""")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString("url")!!
        }
        viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web_view_login, container, false)
    }

    @JavascriptInterface
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtils.resizeStatusBar(context!!.applicationContext, view.findViewById(R.id.v_status))

        if (url != "") {
            et_url.setText(url)
            startVisit()
        } else {
            val url = PreferenceUtils.getStringFromSP(activity!!.applicationContext, "school_url", "")
            if (url != "") {
                et_url.setText(url)
            } else {
                et_url.setText("https://www.baidu.com")
            }
            startVisit()
        }

        if (viewModel.importType == "apply") {
            tv_tips.text = "1. 在上方输入教务网址，部分学校需要连接校园网\n2. 登录后点击到个人课表或者相关的页面\n3. 点击右下角的按钮抓取源码，并上传到服务器"
        }

        if (viewModel.school == "强智教务") {
            cg_qz.visibility = View.VISIBLE
            chip_qz1.isChecked = true
        } else {
            cg_qz.visibility = View.GONE
        }

        if (viewModel.school == "正方教务") {
            cg_zf.visibility = View.VISIBLE
            chip_zf1.isChecked = true
        } else {
            cg_zf.visibility = View.GONE
        }

        if (viewModel.importType == Common.TYPE_HNUST) {
            cg_old_qz.visibility = View.VISIBLE
            chip_old_qz2.isChecked = true
            viewModel.oldQzType = 1
        } else {
            cg_old_qz.visibility = View.GONE
        }

        wv_course.settings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv_course.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        wv_course.addJavascriptInterface(InJavaScriptLocalObj(), "local_obj")
        wv_course.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed() //接受所有网站的证书
            }

        }
        wv_course.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    pb_load.progress = newProgress
                    pb_load.visibility = View.GONE
                    // Toasty.info(activity!!, wv_course.url, Toast.LENGTH_LONG).show()
                } else {
                    pb_load.progress = newProgress * 5
                    pb_load.visibility = View.VISIBLE
                }
            }
        }
        // 设置自适应屏幕，两者合用
        wv_course.settings.useWideViewPort = true //将图片调整到适合WebView的大小
        wv_course.settings.loadWithOverviewMode = true // 缩放至屏幕的大小
        // 缩放操作
        wv_course.settings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        wv_course.settings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        wv_course.settings.displayZoomControls = false //隐藏原生的缩放控件wvCourse.settings
        wv_course.settings.javaScriptCanOpenWindowsAutomatically = true
        wv_course.settings.domStorageEnabled = true
        wv_course.settings.userAgentString = wv_course.settings.userAgentString.replace("Mobile", "eliboM").replace("Android", "diordnA")
        wv_course.settings.textZoom = 100
        initEvent()
    }

    private fun initEvent() {

        var qzChipId = R.id.chip_qz1
        cg_qz.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_qz1 -> {
                    qzChipId = id
                    viewModel.qzType = 0
                }
                R.id.chip_qz2 -> {
                    qzChipId = id
                    viewModel.qzType = 1
                }
                R.id.chip_qz3 -> {
                    qzChipId = id
                    viewModel.qzType = 2
                }
                R.id.chip_qz4 -> {
                    qzChipId = id
                    viewModel.qzType = 3
                }
                R.id.chip_qz5 -> {
                    qzChipId = id
                    viewModel.qzType = 4
                }
                R.id.chip_qz6 -> {
                    qzChipId = id
                    viewModel.qzType = 5
                }
                R.id.chip_qz7 -> {
                    qzChipId = id
                    viewModel.qzType = 6
                }
                else -> {
                    chipGroup.findViewById<Chip>(qzChipId).isChecked = true
                }
            }
        }

        var zfChipId = R.id.chip_zf1
        cg_zf.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_zf1 -> {
                    zfChipId = id
                    viewModel.zfType = 0
                }
                R.id.chip_zf2 -> {
                    zfChipId = id
                    viewModel.zfType = 1
                }
                else -> {
                    chipGroup.findViewById<Chip>(zfChipId).isChecked = true
                }
            }
        }

        var oldQZChipId = R.id.chip_old_qz2
        cg_old_qz.setOnCheckedChangeListener { chipGroup, id ->
            when (id) {
                R.id.chip_old_qz1 -> {
                    oldQZChipId = id
                    viewModel.oldQzType = 0
                }
                R.id.chip_old_qz2 -> {
                    oldQZChipId = id
                    viewModel.oldQzType = 1
                }
                else -> {
                    chipGroup.findViewById<Chip>(oldQZChipId).isChecked = true
                }
            }
        }

        tv_got_it.setOnClickListener {
            tv_got_it.visibility = View.GONE
            tv_tips.visibility = View.GONE
            tv_tips.visibility = View.GONE
        }

        tv_go.setOnClickListener {
            startVisit()
        }

        et_url.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                startVisit()
            }
            return@setOnEditorActionListener false
        }

        val js = "javascript:var ifrs=document.getElementsByTagName(\"iframe\");" +
                "var iframeContent=\"\";" +
                "for(var i=0;i<ifrs.length;i++){" +
                "iframeContent=iframeContent+ifrs[i].contentDocument.body.parentElement.outerHTML;" +
                "}\n" +
                "var frs=document.getElementsByTagName(\"frame\");" +
                "var frameContent=\"\";" +
                "for(var i=0;i<frs.length;i++){" +
                "frameContent=frameContent+frs[i].contentDocument.body.parentElement.outerHTML;" +
                "}\n" +
                "window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML + iframeContent + frameContent);"

        fab_import.setOnClickListener {
            if (viewModel.importType == Common.TYPE_HNUST) {
                if (!isRefer) {
                    val referUrl = when (viewModel.school) {
                        "湖南科技大学" -> "http://kdjw.hnust.cn/kdjw/tkglAction.do?method=goListKbByXs&istsxx=no"
                        "湖南科技大学潇湘学院" -> "http://xxjw.hnust.cn:8080/xxjw/tkglAction.do?method=goListKbByXs&istsxx=no"
                        else -> getHostUrl() + "tkglAction.do?method=goListKbByXs&istsxx=no"
                    }
                    wv_course.loadUrl(referUrl)
                    it.longSnack("请在看到网页加载完成后，再点一次右下角按钮")
                    isRefer = true
                } else {
                    wv_course.loadUrl(js)
                }
            } else if (viewModel.importType == Common.TYPE_CF) {
                if (!isRefer) {
                    val referUrl = getHostUrl() + "xsgrkbcx!getXsgrbkList.action"
                    wv_course.loadUrl(referUrl)
                    it.longSnack("请重新选择一下学期再点按钮导入，要记得选择全部周，记得点查询按钮")
                    isRefer = true
                } else {
                    wv_course.loadUrl(js)
                }
            } else if (viewModel.importType == Common.TYPE_URP) {
                if (!isRefer) {
                    val referUrl = getHostUrl() + "xkAction.do?actionType=6"
                    wv_course.loadUrl(referUrl)
                    it.longSnack("请在看到网页加载完成后，再点一次右下角按钮")
                    isRefer = true
                } else {
                    wv_course.loadUrl(js)
                }
            } else if (viewModel.importType == Common.TYPE_URP_NEW) {
                if (!isRefer) {
                    val referUrl = getHostUrl() + "student/courseSelect/thisSemesterCurriculum/callback"
                    wv_course.loadUrl(referUrl)
                    it.longSnack("请在看到网页加载完成后，再点一次右下角按钮")
                    isRefer = true
                } else {
                    wv_course.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('html')[0].innerText);")
                }
            } else {
                wv_course.loadUrl(js)
            }
        }

        btn_back.setOnClickListener {
            if (wv_course.canGoBack()) {
                wv_course.goBack()
            }
        }
    }

    private fun getHostUrl(): String {
        var url = wv_course.url
        if (!url.endsWith('/')) {
            url += "/"
        }
        return hostRegex.find(wv_course.url)?.value ?: wv_course.url
    }

    private fun startVisit() {
        wv_course.visibility = View.VISIBLE
        ll_error.visibility = View.GONE
        val url = if (et_url.text.toString().startsWith("http://") || et_url.text.toString().startsWith("https://"))
            et_url.text.toString() else "http://" + et_url.text.toString()
        if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
            wv_course.loadUrl(url)
            PreferenceUtils.saveStringToSP(activity!!.applicationContext, "school_url", url)
        } else {
            Toasty.error(context!!.applicationContext, "请输入正确的网址╭(╯^╰)╮").show()
        }
    }

    internal inner class InJavaScriptLocalObj {
        @JavascriptInterface
        fun showSource(html: String) {
            // Log.d("源码", html)
            if (viewModel.importType != "apply") {
                launch {
                    try {
                        val result = viewModel.importSchedule(html)
                        Toasty.success(activity!!,
                                "成功导入 $result 门课程(ﾟ▽ﾟ)/\n请在右侧栏切换后查看").show()
                        activity!!.setResult(RESULT_OK)
                        activity!!.finish()
                    } catch (e: Exception) {
                        Toasty.error(activity!!,
                                "导入失败>_<\n${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                launch {
                    val task = withContext(Dispatchers.IO) {
                        try {
                            viewModel.postHtml(
                                    school = viewModel.schoolInfo[0],
                                    type = if (viewModel.isUrp) "URP" else viewModel.schoolInfo[1],
                                    qq = viewModel.schoolInfo[2],
                                    html = html)
                        } catch (e: Exception) {
                            "上传失败>_<\n" + e.message
                        }
                    }

                    when (task) {
                        "ok" -> {
                            Toasty.success(activity!!.applicationContext, "上传源码成功~请等待适配哦", Toast.LENGTH_LONG).show()
                            activity!!.start<ApplyInfoActivity>()
                            activity!!.finish()
                        }
                        else -> {
                            Toasty.error(activity!!.applicationContext, task, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        wv_course?.webViewClient = null
        wv_course?.webChromeClient = null
        wv_course?.clearCache(true)
        wv_course?.clearHistory()
        wv_course?.removeAllViews()
        wv_course?.destroy()
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(url: String = "") =
                WebViewLoginFragment().apply {
                    arguments = Bundle().apply {
                        putString("url", url)
                    }
                }
    }
}
