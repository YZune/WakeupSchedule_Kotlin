package com.suda.yzune.wakeupschedule.schedule_import

import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.suda.yzune.wakeupschedule.utils.MyRetrofitUtils
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.net.URLEncoder

class ImportRepository(url: String) {
    var VIEWSTATE_POST_CODE = Array<String>(1) { "" }
    //var VIEWSTATE_LOGIN_CODE = "dDwtMTE5ODQzMDQ1NDt0PDtsPGk8MT47PjtsPHQ8O2w8aTw0PjtpPDc+O2k8OT47PjtsPHQ8cDw7cDxsPHZhbHVlOz47bDxcZTs+Pj47Oz47dDxwPDtwPGw8b25jbGljazs+O2w8d2luZG93LmNsb3NlKClcOzs+Pj47Oz47dDx0PDs7bDxpPDI+Oz4+Ozs+Oz4+Oz4+Oz5527rVtbyXbkyZdrm5O4U8rQ4EHA=="
    val VIEWSTATE_LOGIN_CODE = "dDwxNTMxMDk5Mzc0Ozs+LxNdKu56vO/J6IPIRPAbc74T3WU="
    val LOGIN_COOKIE = Array<String>(1) { "" }
    var checkCode = MutableLiveData<Bitmap>()
    var loginResponse = MutableLiveData<String>()
    var prepareResponse = MutableLiveData<String>()
    var scheduleResponse = MutableLiveData<String>()
    var postHtmlResponse = MutableLiveData<String>()
    var schoolInfo = Array<String>(2) { "" }

    private val retrofit = Retrofit.Builder().baseUrl(url).build()
    private val importService = retrofit.create(ImportService::class.java)

    fun checkCode() {
        importService.getCheckCode()
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                        val verificationCode = response?.body()?.bytes()
                        checkCode.value = BitmapFactory.decodeByteArray(verificationCode, 0, verificationCode!!.size)
                        LOGIN_COOKIE[0] = response.headers().values("Set-Cookie").joinToString("; ")
                        Log.d("获取", LOGIN_COOKIE[0])
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        checkCode.value = null
                    }
                })
    }

    fun login(xh: String, pwd: String, code: String) {
        importService.login(
                xh = xh, pwd = pwd, code = code,
                b = "", view_state = VIEWSTATE_LOGIN_CODE,
                cookies = LOGIN_COOKIE[0], textbox1 = "",
                rbl = "学生", lang = "", h = "", hs = ""
        ).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                loginResponse.value = "Failure"
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                loginResponse.value = response?.body()?.string()
                Log.d("返回", response?.body()?.string())
                //VIEWSTATE_POST_CODE.value = parseViewStateCode(loginResponse.value!!)
            }
        })
    }

    fun getPrepare(xh: String) {
        importService.getPrepare(
                xh = xh, referer = "http://xk.suda.edu.cn/xskbcx.aspx?xh=$xh",
                cookies = LOGIN_COOKIE[0]
        ).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                prepareResponse.value = "Failure"
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                prepareResponse.value = response?.body()!!.string()
                VIEWSTATE_POST_CODE[0] = parseViewStateCode(prepareResponse.value!!)
                Log.d("课表", prepareResponse.value!!.substring(prepareResponse.value!!.indexOf("周一")))
            }
        })
    }

    fun toSchedule(xh: String, name: String, year: String, term: String) {
        importService.getSchedule(
                xh = xh, name = URLEncoder.encode(name, "gb2312"), gnmkdm = "N121603",
                event_target = "xnd",
                event_argument = "",
                view_state = VIEWSTATE_POST_CODE[0],
                //view_state = "dDwxNTM4MTc2MTcwO3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDE+O2k8Mj47aTw0PjtpPDc+O2k8OT47aTwxMT47aTwxMz47aTwxNT47aTwyMj47aTwyNj47aTwyOD47aTwzMD47aTwzND47aTwzNj47aTw0MD47PjtsPHQ8cDxwPGw8VGV4dDs+O2w8XGU7Pj47Pjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs+O2w8eG47eG47Pj47Pjt0PGk8Mj47QDwyMDE4LTIwMTk7MjAxNy0yMDE4Oz47QDwyMDE4LTIwMTk7MjAxNy0yMDE4Oz4+O2w8aTwwPjs+Pjs7Pjt0PHQ8OztsPGk8MT47Pj47Oz47dDxwPHA8bDxUZXh0Oz47bDzlrablj7fvvJoxNzI3NDA1MTQ1Oz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlp5PlkI3vvJrpmYjlgaU7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpumZou+8muiuoeeul+acuuenkeWtpuS4juaKgOacr+WtpumZojs+Pjs+Ozs+O3Q8cDxwPGw8VGV4dDs+O2w85LiT5Lia77ya6K6h566X5py657G777yI5Lq65bel5pm66IO977yJOz4+Oz47Oz47dDxwPHA8bDxUZXh0Oz47bDzooYzmlL/nj63vvJrorqExN+iuoeeul+acuuexu+S6uuW3peaZuuiDvTs+Pjs+Ozs+O3Q8O2w8aTwxPjtpPDM+Oz47bDx0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDxAMDw7Ozs7Ozs7Ozs7Pjs7Pjs+Pjs+Pjs+Pjt0PDtsPGk8MT47PjtsPHQ8QDA8Ozs7Ozs7Ozs7Oz47Oz47Pj47dDxwPGw8VmlzaWJsZTs+O2w8bzxmPjs+PjtsPGk8MT47PjtsPHQ8QDA8Ozs7Ozs7Ozs7Oz47Oz47Pj47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE+O2k8MD47aTwwPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs+Ozs+O3Q8O2w8aTwwPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA+O2w8Pjs+Pjs+Ozs7Ozs7Ozs7Oz47Oz47Pj47Pj47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE+O2k8MD47aTwwPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs+Ozs+O3Q8O2w8aTwwPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA+O2w8Pjs+Pjs+Ozs7Ozs7Ozs7Oz47Oz47Pj47Pj47Pj47Pj47PogQr2CrNA7vVQ5D3L5Kqt+RHZ5C",
                cookies = LOGIN_COOKIE[0],
                referer = "http://xk.suda.edu.cn/xskbcx.aspx?xh=" + xh + "&xm=" + URLEncoder.encode(name, "gb2312") + "&gnmkdm=N121603",
                xnd = year,
                xqd = term
        ).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.d("课表", "失败")
                scheduleResponse.value = "Failure"
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val result = response?.body()?.string()
                if (result != null) {
                    scheduleResponse.value = result
                    Log.d("课表", result.substring(result.indexOf("星期一")))
                } else {
                    scheduleResponse.value = null
                }
            }

        })
    }

    fun postHtml(school: String, type: String, html: String) {
        MyRetrofitUtils.instance.getService().postHtml(school, type, html).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                postHtmlResponse.value = "Failure"
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val result = response.body()?.string()
                if (result != null) {
                    postHtmlResponse.value = result
                }
            }

        })
    }

    fun parseViewStateCode(html: String): String {
        var code = ""
        val doc = Jsoup.parse(html)
        val inputs = doc.getElementsByAttributeValue("name", "__VIEWSTATE")
        if (inputs.size > 0) {
            code = inputs[0].attr("value")
            Log.d("找", "finded __VIEWSTATE code=$code")
        } else {
            Log.d("找", "Not find __VIEWSTATE code")
        }
        return code
    }
}