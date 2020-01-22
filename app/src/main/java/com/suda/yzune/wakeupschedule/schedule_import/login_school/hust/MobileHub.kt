package com.suda.yzune.wakeupschedule.schedule_import.login_school.hust

import com.suda.yzune.wakeupschedule.schedule_import.exception.NetworkErrorException
import com.suda.yzune.wakeupschedule.schedule_import.exception.PasswordErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.math.BigInteger

class MobileHub(private var user: String, private var password: String) {
    private val loginUrl = "https://pass.hust.edu.cn/cas/login?service=http%3A%2F%2Fhub.m.hust.edu.cn%2Fcj%2Findex.jsp"
    private val getScheduleUrl = "http://hub.m.hust.edu.cn/kcb/todate/namecourse.action"

    private val headers = Headers.Builder()
            .add("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
            .add("Origin", "pass.hust.edu.cn")
            .add("Upgrade-Insecure-Requests", "1")
            .build()
    private val regexModulus = "\"10001\",\"\",\"(.+?)\"".toRegex()
    private val regexExecution = "name=\"execution\" value=\"(.+?)\"".toRegex()
    private val cookieStore = HashMap<String, List<Cookie>>()

    private var httpClient = OkHttpClient.Builder()
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                    cookieStore.put(url.host(), cookies)
                }

                override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                    val cookies = cookieStore[url.host()]

                    val ret = cookies?.toMutableList() ?: ArrayList()
                    return ret
                }
            })
            .build()
    private lateinit var modulus: String
    private lateinit var execution: String

    lateinit var courseHTML: String

    init {
        user = user.toUpperCase()
    }

    private suspend fun refreshSession() {
        val request = Request.Builder()
                .url(loginUrl)
                .headers(headers)
                .get()
                .build()

        val response = withContext(Dispatchers.IO) { httpClient.newCall(request).execute() }
        val bodyString = withContext(Dispatchers.IO) { response.body()!!.string() }

        var matchResult: MatchResult = regexModulus.find(bodyString) ?: throw Exception("页面加载失败")
        modulus = matchResult.groupValues.last()

        matchResult = regexExecution.find(bodyString) ?: throw Exception("页面加载失败")
        execution = matchResult.groupValues.last()
    }

    suspend fun login() {
        refreshSession()

        val cipher = Cipher(HUST_RSA_EXPONENT, BigInteger(modulus, 16))
        val encryptedUsername = cipher.encrypt(user)
        val encryptedPassword = cipher.encrypt(password)

        val formBody = FormBody.Builder()
                .add("username", encryptedUsername)
                .add("password", encryptedPassword)
                .add("execution", execution)
                .add("code", "code")
                .add("lt", "LT-NeusoftAlwaysValidTicket")
                .add("_eventId", "submit")
                .build()


        val request = Request.Builder()
                .url(loginUrl)
                .headers(headers)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build()

        val response = withContext(Dispatchers.IO) { httpClient.newCall(request).execute() }

        if (response.request().url().toString().contains("login")) {
            throw PasswordErrorException("学号或密码错误，请检查后再输入")
        }
    }

    suspend fun getCourseSchedule() {
        val request = Request.Builder()
                .url(getScheduleUrl)
                .headers(headers)
                .get()
                .build()

        val response = withContext(Dispatchers.IO) { httpClient.newCall(request).execute() }

        courseHTML = withContext(Dispatchers.IO) { response.body()!!.string() }

        if (courseHTML.contains("failed to connect")) {
            throw NetworkErrorException("无法访问HUB系统，请检查是否连接校园网")
        }
    }
}