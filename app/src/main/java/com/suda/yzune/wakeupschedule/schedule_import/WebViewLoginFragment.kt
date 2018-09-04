package com.suda.yzune.wakeupschedule.schedule_import


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.Toast
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_web_view_login.*


class WebViewLoginFragment : Fragment() {

    private val GET_FRAME_CONTENT_STR = "document.getElementById('iframeautoheight').contentWindow.document.body.innerHTML"

    private lateinit var type: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_web_view_login, container, false)
    }

    @JavascriptInterface
    override fun onResume() {
        super.onResume()
        ViewUtils.resizeStatusBar(context!!.applicationContext, v_status)
        wv_course.settings.javaScriptEnabled = true
        wv_course.addJavascriptInterface(InJavaScriptLocalObj(), "local_obj")
        wv_course.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                ll_error.visibility = View.VISIBLE
                wv_course.visibility = View.GONE
            }
        }
        wv_course.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    pb_load.progress = newProgress
                    pb_load.visibility = View.GONE
                } else {
                    pb_load.progress = newProgress * 5
                    pb_load.visibility = View.VISIBLE
                }
            }
        }
        //设置自适应屏幕，两者合用
        wv_course.settings.useWideViewPort = true //将图片调整到适合WebView的大小
        wv_course.settings.loadWithOverviewMode = true // 缩放至屏幕的大小
        // 缩放操作
        wv_course.settings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        wv_course.settings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        wv_course.settings.displayZoomControls = false //隐藏原生的缩放控件
        initEvent()
    }

    private fun initEvent() {
        tv_got_it.setOnClickListener {
            tv_got_it.visibility = View.GONE
            tv_tips.visibility = View.GONE
            v_tips.visibility = View.GONE
        }

        tv_go.setOnClickListener {
            startVisit()
        }

        et_url.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                startVisit()
            }
            false
        }

        fab_import.setOnClickListener {
            wv_course.loadUrl("javascript:var ifrs=document.getElementsByTagName(\"iframe\");" +
                    "var iframeContent=\"\";" +
                    "for(var i=0;i<ifrs.length;i++){" +
                    "iframeContent=iframeContent+ifrs[i].contentDocument.body.parentElement.outerHTML;" +
                    "}" +
                    "var frs=document.getElementsByTagName(\"frame\");" +
                    "var frameContent=\"\";" +
                    "for(var j=0;j<frs.length;j++){" +
                    "var contents = frs[j].contentDocument.getElementsByTagName(\"iframe\");" +
                    "for(var a=0;a<contents.length;a++){" +
                    "frameContent=frameContent+contents[a].contentDocument.body.parentElement.outerHTML;}" +
                    "}" +
                    "window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML + iframeContent + frameContent);")
        }
    }

    private fun startVisit() {
        wv_course.visibility = View.VISIBLE
        ll_error.visibility = View.GONE
        val url = if (et_url.text.toString().startsWith("http://") || et_url.text.toString().startsWith("https://"))
            et_url.text.toString() else "http://" + et_url.text.toString()
        if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
            wv_course.loadUrl(url)
        } else {
            Toasty.error(context!!.applicationContext, "请输入正确的网址╭(╯^╰)╮").show()
        }
    }

    internal inner class InJavaScriptLocalObj {
        @JavascriptInterface
        fun showSource(html: String) {
            if (html.contains("星期一") && html.contains("星期二")) {
                val viewModel = ViewModelProviders.of(activity!!).get(ImportViewModel::class.java)
                if (type == "FZ") {
                    viewModel.importBean2CourseBean(viewModel.html2ImportBean(html), "", context!!.applicationContext, html)
                } else if (type == "newFZ") {
                    viewModel.parseNewFZ(html, "", context!!.applicationContext)
                }
            } else if (type == "apply") {

            } else {
                Toasty.info(context!!.applicationContext, "你貌似还没有点到个人课表哦", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        wv_course.clearCache(true)
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param0: String) =
                WebViewLoginFragment().apply {
                    type = param0
                }
    }
}
