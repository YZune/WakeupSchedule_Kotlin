package com.suda.yzune.wakeupschedule.schedule_import.login_school.jlu

import com.suda.yzune.wakeupschedule.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.TimeUnit


class UIMS(private var user: String, private var pass: String) {

    lateinit var cookie1: String
    lateinit var cookie3: String
    lateinit var cookie4: String
    lateinit var studentId: String
    lateinit var jssionID: String
    lateinit var jssionID2: String
    lateinit var adcId: String

    lateinit var termId: String

    lateinit var courseJSON: JSONObject

    private var httpClient: OkHttpClient = OkHttpClient.Builder()
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(httpUrl: HttpUrl, list: List<Cookie>) {}

                override fun loadForRequest(httpUrl: HttpUrl): List<Cookie> {
                    return ArrayList()
                }
            })
            .followRedirects(false)
            .followSslRedirects(false)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()

    lateinit var builder: MultipartBody.Builder
    private val mediaType = MediaType.parse("application/json; charset=utf-8")

    suspend fun connectToUIMS() {
        builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val request = Request.Builder()
                .url(Address.hostAddress + "/ntms/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                .build()

        val response = withContext(Dispatchers.IO) {
            httpClient.newCall(request).execute()
        }

        var str = response.headers().get("Set-Cookie")
        str = str!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        jssionID = str.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        cookie1 = "loginPage=userLogin.jsp; alu=$user; pwdStrength=1; JSESSIONID=$jssionID"
    }


    suspend fun login() {
        val formBody = FormBody.Builder()
                .add("j_username", user)
                .add("j_password", Utils.getMD5Str("UIMS$user$pass"))
                .add("mousePath", "SGwABSAgCeSBgCwSCQDASCgDRSDQDhSDwDzSEQEDSFgEUSGgEkSHgE1SIgFGSJwFWSLwFnSMwF4SOAGJSPAGZSQQGqSRQG6SSQHMSTgHcSUgHtSVgH9SWgIOSYQIeSZgIvSbAJAScQJRSdwJiSewJySgAKDShAKUShgKkSigK0SjQLFSkALWSlgLmSmgL4SnwMISowMZSpgMpSqQM6SrQNLSsQNbStANsStwN9SvAOOSvgOfSxAOvSxwPASywPRSzwPhS1APyS2AQCS3QQTS4QQkS5QQ1S6QRFARwXr")
                .build()

        val request = Request.Builder()
                .url(Address.hostAddress + "/ntms/j_spring_security_check")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                .header("Cookie", cookie1)
                .header("Referer", Address.hostAddress + "/ntms/userLogin.jsp?reason=nologin")
                .post(formBody)
                .build()
        val response = withContext(Dispatchers.IO) {
            httpClient.newCall(request).execute()
        }
        var str = response.headers().get("Set-Cookie")
        str = str!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        jssionID2 = str.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        cookie3 = "loginPage=userLogin.jsp; alu=$user; pwdStrength=1; JSESSIONID=$jssionID2"
        cookie4 = "loginPage=userLogin.jsp; alu=$user; JSESSIONID=$jssionID2"
    }


    suspend fun getCurrentUserInfo() {
        val formBody = FormBody.Builder().build()
        val request = Request.Builder()
                .url(Address.hostAddress + "/ntms/action/getCurrentUserInfo.do")
                .header("Referer", Address.hostAddress + "/ntms/index.do")
                .header("Connection", "keep-alive")
                .header("Origin", Address.hostAddress)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                .header("Cookie", cookie3)
                .post(formBody)
                .build()

        val response = withContext(Dispatchers.IO) { httpClient.newCall(request).execute() }

        val bufferedReader = BufferedReader(
                InputStreamReader(response.body()?.byteStream(), "UTF-8"), 8 * 1024)
        val entityStringBuilder = StringBuilder()

        withContext(Dispatchers.IO) {
            var line = bufferedReader.readLine()
            while (line != null) {
                entityStringBuilder.append(line + "\n")
                line = bufferedReader.readLine()
            }
        }

        val obj = JSONObject(entityStringBuilder.toString())

        val defRes = obj.get("defRes") as JSONObject
        studentId = defRes.getString("personId")
        termId = defRes.getString("term_l")
        adcId = defRes.getString("adcId")
    }

    suspend fun getCourseSchedule() {
        val params = JSONObject()
        params.put("termId", termId)
        params.put("studId", studentId)

        val jsonObject = JSONObject()
        jsonObject.put("tag", "teachClassStud@schedule")
        jsonObject.put("branch", "default")
        jsonObject.put("params", params)

        val requestBody = RequestBody.create(mediaType, jsonObject.toString())

        val request = Request.Builder()
                .url(Address.hostAddress + "/ntms/service/res.do")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 9.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36")
                .header("Cookie", cookie3)
                .header("Host", Address.host)
                .header("Origin", Address.hostAddress)
                .header("Content-Type", "application/json")
                .header("Referer", Address.hostAddress + "/ntms/index.do")
                .post(requestBody)
                .build()
        val response = withContext(Dispatchers.IO) { httpClient.newCall(request).execute() }
        val bufferedReader = BufferedReader(
                InputStreamReader(response.body()!!.byteStream(), "UTF-8"), 8 * 1024)
        val entityStringBuilder = StringBuilder()
        withContext(Dispatchers.IO) {
            var line = bufferedReader.readLine()
            while (line != null) {
                entityStringBuilder.append(line + "\n")
                line = bufferedReader.readLine()
            }
        }
        courseJSON = JSONObject(entityStringBuilder.toString())
    }
}

