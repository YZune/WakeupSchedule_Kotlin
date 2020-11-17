package com.suda.yzune.wakeupschedule.schedule_import.login_school.nau

import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.schedule_import.Common
import com.suda.yzune.wakeupschedule.schedule_import.exception.NetworkErrorException
import com.suda.yzune.wakeupschedule.schedule_import.exception.PasswordErrorException
import com.suda.yzune.wakeupschedule.schedule_import.exception.ServerErrorException
import com.suda.yzune.wakeupschedule.schedule_import.login_school.SimpleCookieJar
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import javax.crypto.Cipher

class NAUCourse(private val userId: String, private val userPw: String) {
    private val cookieJar: SimpleCookieJar = SimpleCookieJar()
    private val client: OkHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()

    companion object {
        private const val JWC_HOST = "jwc.nau.edu.cn"
        private const val SSO_HOST = "sso.nau.edu.cn"
        private const val JWC_LOGIN_SINGLE_URL = "http://$JWC_HOST/Login_Single.aspx"
        private const val JWC_SSO_LOGIN_URL = "http://$SSO_HOST/sso/login?service=$JWC_LOGIN_SINGLE_URL"

        @Suppress("SpellCheckingInspection")
        private const val JWC_LOGOUT_URL = "http://$JWC_HOST/Loginout.aspx"
        private const val SSO_LOGOUT_URL = "http://$SSO_HOST/sso/logout"

        private const val JWC_COURSE_THIS_TERM_URL = "http://$JWC_HOST/Students/MyCourseScheduleTable.aspx"

        private const val JWC_ALREADY_LOGIN = "已经登录"
        private const val SSO_ACCOUNT_LOCK = "账号被锁定"
        private const val SSO_LOGIN_PASSWORD_ERROR_STR = "密码错误"

        private const val JWC_URL_PARAM_R = "r"
        private const val JWC_URL_PARAM_D = "d"

        private const val HTML_ATTR_NAME = "name"
        private const val HTML_ATTR_VALUE = "value"
        private const val HTML_DIV = "div"

        private const val SSO_USER_PASSWORD_ENCRYPTED = "encrypted"
        private const val SSO_INPUT = "#fm1 > $HTML_DIV:nth-child(5)"
        private const val SSO_POST_FORMAT = "input[$HTML_ATTR_NAME=%s]"
        private const val SSO_POST_USERNAME = "username"
        private const val SSO_POST_PASSWORD = "password"
        private val SSO_LOGIN_PARAM = arrayOf("execution", "_eventId", "loginType", "submit")

        private const val RSA_ALGORITHM_NAME = "RSA"
        private const val RSA_EXPONENT = "010001"

        @Suppress("SpellCheckingInspection")
        private const val RSA_MODULUS =
                "008aed7e057fe8f14c73550b0e6467b023616ddc8fa91846d2613cdb7f7621e3cada4cd5d812d627af6b87727ade4e26d26208b7326815941492b2204c3167ab2d53df1e3a2c9153bdb7c8c2e968df97a5e7e01cc410f92c4c2c2fba529b3ee988ebc1fca99ff5119e036d732c368acf8beba01aa2fdafa45b21e4de4928d0d403"


        private fun encryptPassword(password: String): String {
            val modulus = BigInteger(RSA_MODULUS, 16)
            val exponent = BigInteger(RSA_EXPONENT, 16)

            val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NAME)
            val publicKey = keyFactory.generatePublic(RSAPublicKeySpec(modulus, exponent)) as RSAPublicKey

            val cipher = Cipher.getInstance(RSA_ALGORITHM_NAME)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            return buildString {
                for (byte in cipher.doFinal(password.reversed().toByteArray())) {
                    Integer.toHexString(byte.toInt() and 0xFF).let {
                        if (it.length == 1) append('0')
                        append(it)
                    }
                }
            }
        }

        private fun buildPostForm(userId: String, userPw: String, ssoResponseContent: String) = FormBody.Builder().apply {
            val htmlContent = Jsoup.parse(ssoResponseContent).select(SSO_INPUT)
            add(SSO_POST_USERNAME, userId)
            add(SSO_POST_PASSWORD, encryptPassword(userPw))
            add(SSO_USER_PASSWORD_ENCRYPTED, true.toString())
            for (param in SSO_LOGIN_PARAM) {
                val input = htmlContent.select(SSO_POST_FORMAT.format(param)).first()
                val value = input.attr(HTML_ATTR_VALUE)
                add(param, value)
            }
        }.build()
    }

    private fun login(reLogin: Boolean = true) {
        client.newCall(Request.Builder().url(JWC_SSO_LOGIN_URL).build()).execute().use { pr ->
            pr.body()?.string()?.let { pb ->
                client.newCall(Request.Builder().url(JWC_SSO_LOGIN_URL).post(buildPostForm(userId, userPw, pb)).build()).execute().use { r1 ->
                    if (r1.isSuccessful) {
                        val u1 = r1.request().url()
                        r1.body()?.string()?.let { b1 ->
                            when (u1.host()) {
                                JWC_HOST -> if (u1.queryParameter(JWC_URL_PARAM_R).isNullOrBlank() || u1.queryParameter(JWC_URL_PARAM_D).isNullOrBlank()) {
                                    if (JWC_ALREADY_LOGIN in b1) {
                                        if (reLogin) {
                                            jwcLogout()
                                            login(false)
                                        } else {
                                            throw ServerErrorException("教务服务器错误！")
                                        }
                                    } else {
                                        throw ServerErrorException("教务未知登录错误！")
                                    }
                                } else {
                                    return
                                }
                                SSO_HOST -> when {
                                    SSO_LOGIN_PASSWORD_ERROR_STR in b1 -> throw PasswordErrorException("密码错误！")
                                    SSO_ACCOUNT_LOCK in b1 -> throw PasswordErrorException("账户被锁定！")
                                    else -> throw ServerErrorException("SSO未知登录错误！")
                                }
                                else -> throw ServerErrorException("SSO登录页面错误！")
                            }
                        }
                    }
                }
            }
        }
        throw NetworkErrorException("SSO服务器连接失败！")
    }

    private fun jwcLogout() = client.newCall(Request.Builder().url(JWC_LOGOUT_URL).build()).execute().use {
        it.isSuccessful
    }

    private fun logout() {
        jwcLogout()
        client.newCall(Request.Builder().url(SSO_LOGOUT_URL).build()).execute().close()
    }

    private fun getCourseTableHtmlContent(): String? {
        client.newCall(Request.Builder().url(JWC_COURSE_THIS_TERM_URL).build()).execute().use {
            if (it.isSuccessful) {
                return it.body()?.string()
            }
        }
        return null
    }

    private fun parseCourseTable(importTableId: Int, htmlContent: String): Pair<List<CourseBaseBean>, List<CourseDetailBean>> {
        val document = Jsoup.parse(htmlContent)
        val courseList = ArrayList<CourseBaseBean>()
        val courseDetailList = ArrayList<CourseDetailBean>()
        if (document != null) {
            val tr = document.body().getElementById("content").getElementsByTag("tr")
            if (!tr.isEmpty()) {
                for (i in 1 until tr.size) {
                    val td = tr[i].getElementsByTag("td")
                    if (td.size < 8) throw IOException()
                    var detail = td[8].text().trim { it <= ' ' }
                    if (!detail.contains("上课地点：")) {
                        continue
                    }
                    detail = detail.substring(detail.indexOf("上课地点：") + "上课地点：".length)
                    val details = detail.split("上课地点：".toRegex()).toTypedArray()

                    var room: String
                    var day: Int
                    var startNode: Int
                    var step: Int
                    var type: Int

                    for (s in details) {
                        val temp = s.split("上课时间：")
                        room = temp[0]
                        val timeTemp = temp[1].split(" ")
                        day = timeTemp[2].toInt()

                        val time = Common.parseTimePeriod(timeTemp[4].substring(0, timeTemp[4].indexOf("节")).trim())
                        startNode = time.first
                        step = time.second - time.first + 1

                        val rawWeekStr = timeTemp[0].trim()
                        val weekStr = when {
                            "单" in rawWeekStr -> {
                                type = 1
                                rawWeekStr.substring(0, rawWeekStr.indexOf("之")).trim()
                            }
                            "双" in rawWeekStr -> {
                                type = 2
                                rawWeekStr.substring(0, rawWeekStr.indexOf("之")).trim()
                            }
                            else -> {
                                type = 0
                                if (rawWeekStr.startsWith("第")) {
                                    rawWeekStr.substring(1, rawWeekStr.indexOf("周")).trim()
                                } else {
                                    rawWeekStr.substring(0, rawWeekStr.indexOf("周")).trim()
                                }
                            }
                        }

                        for (week in Common.parseTimePeriodList(weekStr)) {
                            courseDetailList.add(CourseDetailBean(
                                    i - 1, day, room, td[7].text(),
                                    startNode, step,
                                    week.first, week.second, type,
                                    importTableId
                            ))
                        }
                    }
                    courseList.add(CourseBaseBean(
                            i - 1, td[2].text(),
                            "#${Integer.toHexString(ViewUtils.getCustomizedColor(index = courseList.size % 9))}",
                            importTableId
                    ))

                }
            }
        }
        return courseList to courseDetailList
    }

    suspend fun getCourseTable(importTableId: Int) = withContext(Dispatchers.IO) {
        try {
            login()
            getCourseTableHtmlContent()?.let {
                return@withContext parseCourseTable(importTableId, it)
            }
            throw NetworkErrorException("无法获取课表信息！")
        } finally {
            logout()
        }
    }
}