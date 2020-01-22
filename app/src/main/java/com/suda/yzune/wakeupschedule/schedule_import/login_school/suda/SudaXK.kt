package com.suda.yzune.wakeupschedule.schedule_import.login_school.suda

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.suda.yzune.wakeupschedule.schedule_import.exception.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Retrofit
import java.net.URLEncoder

class SudaXK {

    var id = ""
    var password = ""
    var code = ""

    var selectedYear = ""
    var selectedTerm = ""
    var selectedSchedule = ""

    val years = arrayListOf<String>()

    private val baseUrl = "http://xk.suda.edu.cn/"
    private val retrofit = Retrofit.Builder().baseUrl(baseUrl).build()
    private val importService = retrofit.create(SudaLoginService::class.java)
    private var loginCookieStr = ""
    private var viewStateLoginCode = "NaVt606u7aQBJEEhk3RWLXGaiMFU9GXaFtEVBpWUA+ra8qyu7k0E4mgRHeyf1hlYsZHs9ngnKBSairsotDxFcaRJOmef0K1SCAQSi+MVffEecCQ2+KSggvkWsQnHndqK3mPhjjJBNLNd/XhJhwt0fXSOYKDAuDql/i2Wrac6K1TmAiU3JYbPjFsAcxRLQW2Hxn4ukBCSYAgt9BbSm54e1Zc/e1Y="
    private var viewStatePostCode = ""
    private var name = ""

    suspend fun getCheckCode(): Bitmap {
        return withContext(Dispatchers.IO) {
            val response = importService.getCheckCode().execute()
            if (response.isSuccessful) {
                val verificationCode = response.body()?.bytes()
                loginCookieStr = response.headers().values("Set-Cookie").joinToString("; ")
                BitmapFactory.decodeByteArray(verificationCode, 0, verificationCode!!.size)
            } else {
                throw NetworkErrorException("请检查是否连接校园网")
            }
        }
    }

    suspend fun login() {
        withContext(Dispatchers.IO) {
            viewStateLoginCode = parseViewStateCode(Jsoup.connect(baseUrl).execute().body())
            val loginResponse = importService.login(
                    xh = id, pwd = password, code = code,
                    b = "", view_state = viewStateLoginCode,
                    cookies = loginCookieStr
            ).execute()
            if (!loginResponse.isSuccessful) throw NetworkErrorException("请检查是否连接校园网")
            val loginResult = loginResponse.body()?.string()
                    ?: throw NetworkErrorException("请检查是否连接校园网")
            when {
                loginResult.contains("验证码不正确") -> throw CheckCodeErrorException("验证码不正确哦")
                loginResult.contains("密码错误") -> throw PasswordErrorException("密码错误哦")
                loginResult.contains("用户名或密码不正确") -> throw UserNameErrorException("看看学号是不是输错啦")
                loginResult.contains("请耐心排队") -> throw QueuingUpException("选课排队中，稍后再试哦")
                loginResult.contains("欢迎您：") || loginResult.contains("同学，你好") -> {
                }
                else -> throw Exception("再试一次看看哦")
            }

            val preResponse = importService.getPrepare(
                    xh = id, referer = "${baseUrl}xskbcx.aspx?xh=$id",
                    cookies = loginCookieStr
            ).execute()
            if (!preResponse.isSuccessful) throw GetTermDataErrorException("获取学期数据失败:(")
            val preResult = preResponse.body()?.string()
                    ?: throw GetTermDataErrorException("获取学期数据失败:(")
            selectedSchedule = preResult
            viewStatePostCode = parseViewStateCode(preResult)
            years.clear()
            years.addAll(parseYears(preResult))
            if (years.isEmpty()) throw GetTermDataErrorException("获取学期数据失败:(")
            name = parseName(preResult)
        }
    }

    suspend fun toSchedule(year: String, term: String): String {
        if (year == selectedYear && term == selectedTerm) return selectedSchedule
        return withContext(Dispatchers.IO) {
            val response = importService.getSchedule(
                    xh = id, name = URLEncoder.encode(name, "gb2312"), gnmkdm = "N121603",
                    event_target = "xnd",
                    event_argument = "",
                    view_state = viewStatePostCode,
                    cookies = loginCookieStr,
                    referer = "${baseUrl}xskbcx.aspx?xh=" + id + "&xm=" + URLEncoder.encode(name, "gb2312") + "&gnmkdm=N121603",
                    xnd = year,
                    xqd = term
            ).execute()
            if (!response.isSuccessful) throw NetworkErrorException("请检查是否连接校园网")
            val result = response.body()?.string() ?: throw NetworkErrorException("请检查是否连接校园网")
            if (result.contains("您本学期课所选学分小于 0分")) throw Exception("该学期貌似还没有课程")
            result
        }
    }

    private fun parseYears(html: String): List<String> {
        val selected = "selected"
        val option = "option"

        val doc = Jsoup.parse(html)
        val years = arrayListOf<String>()

        val selects = doc.getElementsByTag("select")
        if (selects == null || selects.size < 2) {
            throw GetTermDataErrorException("获取学期数据失败:(")
        }

        var options = selects[0].getElementsByTag(option)

        for (o in options) {
            val year = o.text().trim()
            years.add(year)
            if (o.attr(selected) == selected) {
                selectedYear = year
            }
        }

        options = selects[1].getElementsByTag(option)
        for (o in options) {
            val term = o.text().trim { it <= ' ' }
            if (o.attr(selected) == selected) {
                selectedTerm = term
            }
        }

        // Log.d("解析", years.toString())
        return years
    }

    private fun parseName(html: String): String {
        val start = html.indexOf(">姓名：")
        return html.substring(start + 4, html.indexOf("</span>", start))
    }

    private fun parseViewStateCode(html: String): String {
        var code = ""
        val doc = Jsoup.parse(html)
        val inputs = doc.getElementsByAttributeValue("name", "__VIEWSTATE")
        if (inputs.size > 0) {
            code = inputs[0].attr("value")
        }
        return code
    }

}