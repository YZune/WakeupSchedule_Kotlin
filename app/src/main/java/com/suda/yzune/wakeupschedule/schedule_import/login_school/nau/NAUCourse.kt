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
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException

class NAUCourse(private val userId: String, private val userPw: String) {
    private val cookieJar: SimpleCookieJar = SimpleCookieJar()
    private val client: OkHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()

    companion object {
        private const val SSO_HOST = "sso.nau.edu.cn"
        private const val SSO_LOGOUT_URL = "http://$SSO_HOST/sso/logout"

        private const val JWC_HOST = "jwc.nau.edu.cn"
        private const val JWC_SSO_LOGIN_URL = "http://$SSO_HOST/sso/login?service=http%3A%2F%2F$JWC_HOST%2FLogin_Single.aspx"
        private const val JWC_LOGOUT_URL = "http://$JWC_HOST/LoginOut.aspx"

        private val SSO_LOGIN_PARAM = arrayOf("lt", "execution", "_eventId", "useVCode", "isUseVCode", "sessionVcode")
        private const val SSO_LOGIN_PARAM_ERROR_COUNT = "errorCount"
        private const val SSO_LOGIN_PARAM_ERROR_COUNT_VALUE = ""

        private const val SSO_INPUT_TAG_NAME_ATTR = "name"
        private const val SSO_INPUT_TAG_VALUE_ATTR = "value"
        private const val SSO_INPUT = "input[$SSO_INPUT_TAG_NAME_ATTR][$SSO_INPUT_TAG_VALUE_ATTR]"
        private const val SSO_POST_FORMAT = "input[$SSO_INPUT_TAG_NAME_ATTR=%s]"
        private const val SSO_POST_USERNAME = "username"
        private const val SSO_POST_PASSWORD = "password"

        private const val JWC_ALREADY_LOGIN_STR = "已经登录"
        private const val JWC_SERVER_ERROR_STR = "非法字符"
        private const val JWC_PASSWORD_ERROR_STR = "密码错误"
        private const val JWC_LOGIN_PAGE_STR = "用户登录"

        private const val JWC_DEFAULT_ASPX = "default.aspx"
        private const val JWC_STUDENTS_PATH = "Students"
        private const val JWC_URL_PARAM_R = "r"
        private const val JWC_URL_PARAM_D = "d"

        private const val SSO_LOGIN_PASSWORD_ERROR_STR = "密码错误"
        private const val SSO_LOGIN_INPUT_ERROR_STR = "请勿输入非法字符"
        private const val SSO_SERVER_ERROR = "单点登录系统未正常工作"
        private const val SSO_LOGIN_PAGE_STR = "南京审计大学统一身份认证登录"

        private const val COURSE_TABLE_URL = "http://$JWC_HOST/Students/MyCourseScheduleTable.aspx"

        private fun getLoginPostForm(userId: String, userPw: String, ssoResponseContent: String): FormBody = FormBody.Builder().apply {
            add(SSO_POST_USERNAME, userId)
            add(SSO_POST_PASSWORD, userPw)
            val htmlContent = Jsoup.parse(ssoResponseContent).select(SSO_INPUT)
            for (param in SSO_LOGIN_PARAM) {
                add(param, htmlContent.select(SSO_POST_FORMAT.format(param)).first().attr(SSO_INPUT_TAG_VALUE_ATTR))
            }
            add(SSO_LOGIN_PARAM_ERROR_COUNT, SSO_LOGIN_PARAM_ERROR_COUNT_VALUE)
        }.build()

        private fun validateJwcLoginUrl(url: HttpUrl): Boolean =
                url.pathSegments().size >= 2 && url.pathSegments()[0] == JWC_STUDENTS_PATH && url.pathSegments()[1] == JWC_DEFAULT_ASPX &&
                        url.querySize() >= 2 && url.queryParameter(JWC_URL_PARAM_D) != null && url.queryParameter(JWC_URL_PARAM_R) != null
    }

    private fun login(reLogin: Boolean = true) {
        client.newCall(Request.Builder().url(JWC_SSO_LOGIN_URL).build()).execute().use { r1 ->
            if (r1.isSuccessful) {
                val u1 = r1.request().url()
                r1.body()?.string()?.let { b1 ->
                    when {
                        u1.host().equals(JWC_HOST, true) -> jwcLoginCheck(u1, b1, reLogin)
                        SSO_LOGIN_PAGE_STR in b1 || u1.host().equals(SSO_HOST, true) ->
                            client.newCall(r1.request().newBuilder().post(getLoginPostForm(userId, userPw, b1)).build()).execute().use { r2 ->
                                if (r2.isSuccessful) {
                                    val u2 = r2.request().url()
                                    r2.body()?.string()?.let { b2 ->
                                        when {
                                            u2.host().equals(JWC_HOST, true) -> {
                                                jwcLoginCheck(u2, b2, reLogin)
                                                return
                                            }
                                            SSO_LOGIN_PASSWORD_ERROR_STR in b2 -> throw PasswordErrorException("密码错误！")
                                            SSO_LOGIN_INPUT_ERROR_STR in b2 -> throw PasswordErrorException("用户名或密码错误！")
                                            SSO_SERVER_ERROR in b2 -> throw ServerErrorException(SSO_SERVER_ERROR)
                                            else -> throw ServerErrorException("SSO未知登录错误！")
                                        }
                                    }
                                }
                            }
                        SSO_SERVER_ERROR in b1 -> throw ServerErrorException(SSO_SERVER_ERROR)
                        else -> throw ServerErrorException("SSO登录页面错误！")
                    }
                }
            }
        }
        throw NetworkErrorException("SSO服务器连接失败！")
    }

    private fun jwcLoginCheck(url: HttpUrl, body: String, reLogin: Boolean) {
        when {
            JWC_PASSWORD_ERROR_STR in body -> throw PasswordErrorException("密码错误！")
            JWC_SERVER_ERROR_STR in body -> throw ServerErrorException(JWC_SERVER_ERROR_STR)
            JWC_LOGIN_PAGE_STR in body || JWC_ALREADY_LOGIN_STR in body -> {
                if (reLogin && jwcLogout()) {
                    login(false)
                } else {
                    throw ServerErrorException("您已在其他地方登录教务系统，请十分钟后重试！")
                }
            }
            !validateJwcLoginUrl(url) -> throw ServerErrorException("教务系统登录页面错误！")
        }
    }

    private fun jwcLogout() = client.newCall(Request.Builder().url(JWC_LOGOUT_URL).build()).execute().use {
        it.isSuccessful
    }

    private fun logout() {
        jwcLogout()
        client.newCall(Request.Builder().url(SSO_LOGOUT_URL).build()).execute().close()
    }

    private fun getCourseTableHtmlContent(): String? {
        client.newCall(Request.Builder().url(COURSE_TABLE_URL).build()).execute().use {
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