package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.SparseArray
import android.util.Xml
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.*
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countStr
import com.suda.yzune.wakeupschedule.utils.CourseUtils.getNodeInt
import com.suda.yzune.wakeupschedule.utils.CourseUtils.intList2WeekBeanList
import com.suda.yzune.wakeupschedule.utils.CourseUtils.isContainName
import com.suda.yzune.wakeupschedule.utils.MyRetrofitUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.xmlpull.v1.XmlPullParser
import retrofit2.Retrofit
import java.io.*
import java.net.URLEncoder
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream


class ImportViewModel(application: Application) : AndroidViewModel(application) {

    var importId = -1
    var newFlag = false
    var isUrp = false
    var zfType = 0
    var qzType = 0
    var oldQzType = 0
    var htmlName = ""
    var htmlPath = ""

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private val timeTableDao = dataBase.timeTableDao()
    private val timeDetailDao = dataBase.timeDetailDao()
    private var hasTypeFlag = false

    private val pattern = "第.*节"
    private val nodePattern = Pattern.compile("\\(\\d{1,2}[-]*\\d*节")
    private val weekPattern = Pattern.compile("\\d{1,2}周")
    private val weekPattern1 = Pattern.compile("\\d{1,2}[-]*\\d*")
    private val nodePattern1 = Pattern.compile("\\d{1,2}[~]*\\d*节")

    private val other = arrayOf("时间", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日", "早晨", "上午", "下午", "晚上")
    private val pattern1 = Pattern.compile("\\{第\\d{1,2}[-]*\\d*周")
    private val WEEK = arrayOf("", "周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val courseProperty = arrayOf("任选", "限选", "实践选修", "必修课", "选修课", "必修", "选修", "专基", "专选", "公必", "公选", "义修", "选", "必", "主干", "专限", "公基", "值班", "通选",
            "思政必", "思政选", "自基必", "自基选", "语技必", "语技选", "体育必", "体育选", "专业基础课", "双创必", "双创选", "新生必", "新生选", "学科必修", "学科选修",
            "通识必修", "通识选修", "公共基础", "第二课堂", "学科实践", "专业实践", "专业必修", "辅修", "专业选修", "外语", "方向", "专业必修课", "全选")
    val oldQZList1 = arrayOf("湖南科技大学", "湖南科技大学潇湘学院")
    val oldQZList = arrayOf("旧强智（需要 IE 的那种）", "湖南工学院")
    val urpList = arrayOf("江西农业大学南昌商学院", "渤海大学", "烟台大学", "山西工程技术学院", "安徽财经大学", "河北工程大学", "中国农业大学",
            "上海海洋大学", "齐鲁师范学院", "山西农业大学", "中国石油大学（北京）", "内蒙古科技大学", "湖南理工学院",
            "内蒙古大学", "齐齐哈尔大学", "河南理工大学", "西南石油大学", "河北大学", "北京邮电大学", "东北财经大学",
            "天津工业大学", "山东农业大学", "河海大学", "URP 系统")
    val ZFSchoolList = arrayOf("西安建筑科技大学", "湖南农业大学", "渭南师范学院", "西安科技大学", "湖南城市学院", "武汉东湖学院", "沈阳师范大学", "厦门工学院", "北京联合大学", "浙江工业大学之江学院", "西安外事学院", "福建农林大学金山学院", "辽宁工业大学", "重庆邮电大学移通学院", "河南工程学院", "黑龙江外国语学院", "四川大学锦城学院", "郑州大学西亚斯国际学院", "安徽大学", "杭州医学院", "河北科技师范学院", "徐州幼儿师范高等专科学校", "海南师范大学", "华北电力大学科技学校", "山东师范大学", "广东海洋大学", "郑州航空工业管理学院", "河北经贸大学", "福建师范大学", "安徽工业大学", "潍坊学院", "大连工业大学艺术与信息工程学院", "华南农业大学", "大连大学", "成都理工大学工程技术学院", "云南财经大学", "重庆三峡学院", "杭州电子科技大学", "北京信息科技大学",
            "绍兴文理学院", "广东环境保护工程职业学院", "西华大学", "西安理工大学", "绍兴文理学院元培学院", "北京工业大学")
    val ZFSchoolList1 = arrayOf("广东科学技术职业学院", "茂名职业技术学院", "福建农林大学", "浙江万里学院", "重庆交通职业学院")
    val newZFSchoolList = arrayOf("华中农业大学", "常州机电职业技术学院", "保定学院", "河北环境工程学院", "安徽信息工程学院", "延安大学", "浙江财经大学", "中国医科大学", "苏州农业职业技术学院", "无锡太湖学院", "山东青年政治学院", "河南财经政法大学", "青岛科技大学", "三江学院", "西昌学院", "滨州医学院", "青岛滨海学院", "天津体育学院", "中国矿业大学徐海学院", "武昌首义学院", "四川轻化工大学", "安徽农业大学", "湖北工程学院新技术学院", "贺州学院", "河北政法职业学院", "浙江工商大学", "淮南师范学院", "广西大学", "湖北中医药大学", "南京城市职业学院", "北京化工大学", "信阳师范学院", "西南政法大学", "广西大学行健文理学院", "江西中医药大学", "嘉兴学院南湖学院", "湖北师范大学", "南宁职业技术学院", "济南大学", "西安邮电大学", "浙江工业大学", "徐州医科大学", "温州医科大学", "浙江农林大学", "中国地质大学（武汉）", "厦门理工学院", "浙江师范大学行知学院", "硅湖职业技术学院", "西南民族大学", "山东理工大学", "江苏工程职业技术学院",
            "南京工业大学", "德州学院", "南京特殊教育师范学院", "济南工程职业技术学院", "吉林建筑大学", "宁波工程学院", "西南大学", "河北师范大学",
            "贵州财经大学", "江苏建筑职业技术学院", "武汉纺织大学", "浙江师范大学",
            "山东政法大学", "石家庄学院", "中国矿业大学", "武汉轻工大学", "黄冈师范学院", "广州大学", "南京师范大学中北学院",
            "湖北经济学院", "华中师范大学", "华南理工大学", "潍坊职业学院")
    val gzChengFangList = arrayOf("南方医科大学", "广东工业大学", "五邑大学", "湖北医药学院")
    val qzCrazyList = arrayOf("河北金融学院", "桂林理工大学博文管理学院", "佛山科学技术学院", "华南农业大学珠江学院", "重庆大学城市科技学院")
    val qzAbnormalNodeList = arrayOf("北京林业大学", "青岛农业大学", "广东金融学院")
    val qzGuangwaiList = arrayOf("哈尔滨工程大学", "北京理工大学", "北京理工大学珠海学院", "江苏师范大学", "广东外语外贸大学", "海南大学", "广州医科大学", "长沙医学院")
    val qzLessNodeSchoolList = arrayOf("大庆师范学院", "吉林师范大学", "锦州医科大学", "中国药科大学", "广西师范学院", "南宁师范大学", "天津中医药大学", "山东大学威海校区",
            "吉首大学", "南京理工大学", "天津医科大学", "重庆交通大学", "沈阳工程学院", "韶关学院")
    val qzMoreNodeSchoolList = arrayOf("电子科技大学中山学院", "中国石油大学胜利学院", "江苏科技大学", "山东大学（威海）", "南昌大学", "湖南工业大学", "南方科技大学", "山东财经大学", "湘潭大学", "哈尔滨商业大学", "山东科技大学", "华东理工大学", "中南大学", "湖南商学院", "威海职业学院", "大连外国语大学",
            "中南林业科技大学", "东北林业大学", "齐鲁工业大学", "四川美术学院", "广东财经大学", "南昌航空大学", "皖西学院", "中南财经政法大学", "临沂大学")
    var selectedYear = ""
    var selectedTerm = ""
    var selectedSchedule = ""
    var schoolInfo = Array(3) { "" }

    private val baseList = arrayListOf<CourseBaseBean>()
    private val detailList = arrayListOf<CourseDetailBean>()
    private val retryList = arrayListOf<Int>()

    private val retrofit = Retrofit.Builder().baseUrl("http://xk.suda.edu.cn/").build()
    private val importService = retrofit.create(ImportService::class.java)
    private var loginCookieStr = ""
    private val viewStateLoginCode = "gL9F+JHumK2sqbV6zwQemFSg4zth6L+4YJUeYQOsGmGYGgicF/OqcZ/3Ocj2R8yHlucjhxo/qkiMTckoHKd1YfTaVtAxBVg5vqINlJUEHgUsbYYrCCMI6PRc83d5awHsV3aHev7t543cfjmKx/YhUT/xj+K2h1OQqFLYYZND8u58U+zuIxTfpVopvsko0oo0JpZkNXtiBfbdJ0lc5OVaUCFBK8E="
    private var viewStatePostCode = ""

    suspend fun getNewId(): Int {
        val lastId = tableDao.getLastIdInThread()
        return if (lastId != null) lastId + 1 else 1
    }

    suspend fun getCheckCode(): Bitmap {
        val response = importService.getCheckCode().execute()
        return if (response.isSuccessful) {
            val verificationCode = response.body()?.bytes()
            loginCookieStr = response.headers().values("Set-Cookie").joinToString("; ")
            BitmapFactory.decodeByteArray(verificationCode, 0, verificationCode!!.size)
        } else {
            throw Exception()
        }
    }

    suspend fun loginTsinghua(username: String, password: String): String {
        //login
        val LEARN_PREFIX = "https://learn2018.tsinghua.edu.cn"
        baseList.clear()
        detailList.clear()
        var cookies: Map<String, String>?
        val ticket = Jsoup.connect("https://id.tsinghua.edu.cn/do/off/ui/auth/login/post/bb5df85216504820be7bba2b0ae1535b/0?/login.do")
                .data("i_user", username).data("i_pass", password).data("atOnce", true.toString())
                .timeout(10000).post()
                .body().select("a").attr("href").split('=').last()
        val loginResponse = Jsoup.connect("$LEARN_PREFIX/b/j_spring_security_thauth_roaming_entry?ticket=$ticket")
                .execute().let {
                    cookies = it.cookies()
                    it.statusCode() in 200..299
                }
        if (!loginResponse) throw Exception("Incorrect username or password.")
        //getSemesterIdList
        val semesterIdArray = JSONArray(Jsoup.connect("$LEARN_PREFIX/b/wlxt/kc/v_wlkc_xs_xktjb_coassb/queryxnxq")
                .cookies(cookies).execute().body()).let {
            Array<String>(it.length()) { i: Int -> it.getString(i) }
        }
        //getCurrentSemester
        var currentSemester = JSONObject(Jsoup.connect("$LEARN_PREFIX/b/kc/zhjw_v_code_xnxq/getCurrentAndNextSemester")
                .cookies(cookies).execute().body())
                .getJSONObject("result").getString("id")
        if (currentSemester.split("-").last() == "3" && semesterIdArray.indexOf(currentSemester) > 0)
            currentSemester = semesterIdArray[semesterIdArray.indexOf(currentSemester) - 1]
        //getCourseList
        val courseList = JSONObject(Jsoup.connect("$LEARN_PREFIX/b/wlxt/kc/v_wlkc_xs_xkb_kcb_extend/student/loadCourseBySemesterId/$currentSemester")
                .cookies(cookies).execute().body())
                .getJSONArray("resultList")
        val courseDetailList = Array<Array<String>>(courseList.length()) { i ->
            JSONArray(Jsoup.connect("$LEARN_PREFIX/b/kc/v_wlkc_xk_sjddb/detail?id=${courseList.getJSONObject(i).getString("wlkcid")}")
                    .cookies(cookies).execute().body()).let {
                Array(it.length()) { idx ->
                    it.getString(idx)
                }
            }
        }
        for (i in 0 until courseList.length()) {
            baseList.add(CourseBaseBean(i,
                    courseName = courseList.getJSONObject(i).getString("kcm"),
                    color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), i % 9))}",
                    tableId = importId
            ))
            for (element in courseDetailList[i]) {
                val matcher = Pattern.compile("星期([一二三四五六七日])第([1-6])节\\((.*?)\\)，(.*)")
                        .matcher(element)
                if (matcher.find()) {
                    val matchRs = matcher.toMatchResult()

                    detailList.add(CourseDetailBean(i,
                            day = "一二三四五六七日".indexOf(matchRs.group(1)) + 1,
                            room = matchRs.group(4),
                            teacher = courseList.getJSONObject(i).getString("jsm"),
                            tableId = importId,
                            startNode = when (matchRs.group(2).toInt()) {
                                1 -> 1; 2 -> 3;3 -> 6;4 -> 8;5 -> 10;6 -> 12
                                else -> 0
                            },
                            step = when (matchRs.group(2).toInt()) {
                                2, 6 -> 3
                                else -> 2
                            },
                            startWeek = if (matchRs.group(3).contains("后")) 9 else 1,
                            endWeek = if (matchRs.group(3).contains("前")) 8 else 16,
                            type = 0
                    ))
                }
            }
        }
        return write2DB()
    }

    suspend fun parseZFNewer(html: String): String {
        baseList.clear()
        detailList.clear()
        var id = 0

        val nodePattern = "\\d+"
        val weekPattern1 = Pattern.compile("(\\d+)-(\\d+)")
        val weekPattern2 = Pattern.compile("(\\d+)")

        val doc = Jsoup.parse(html)

        val table1 = doc.getElementById("table1")
        val trs = table1.getElementsByTag("tr")

        var node = 0
        var teacher = ""
        var room = ""
        var step = 1
        var startWeek = 0
        var endWeek = 0
        var type = 0
        for (tr in trs) {
            var countFlag = false
            var countDay = 1
            val tds = tr.getElementsByTag("td")
            for (td in tds) {
                val courseValue = td.text().trim()
                if (inArray(other, courseValue)) {
                    //other data
                    continue
                }
                if (courseValue.isEmpty()) {
                    if (countFlag) {
                        countDay++
                    }
                    continue
                }
                if (Pattern.matches(nodePattern, courseValue)) {
                    node = courseValue.toInt()
                    countFlag = true
                    continue
                }

                val infos = td.html().substringAfter("</span>").substringBeforeLast("<br>").split("<br>")

                val courseName = infos[0]

                for (i in 1 until infos.size step 2) {
                    if (i + 1 >= infos.size) continue
                    if (!infos[i].contains('{') || !infos[i].contains('}')) continue
                    teacher = infos[i].substringBefore('{')
                    room = infos[i + 1]
                    step = room.substringAfterLast('(').substringBeforeLast('节').toInt()
                    val weekList = infos[i].substringAfter('{').substringBefore('}').split(',')
                    weekList.forEach {
                        if (it.contains('-')) {
                            val matcher = weekPattern1.matcher(it)
                            matcher.find()
                            startWeek = matcher.group(1).toInt()
                            endWeek = matcher.group(2).toInt()

                            type = when {
                                it.contains('单') -> 1
                                it.contains('双') -> 2
                                else -> 0
                            }
                        } else {
                            val matcher = weekPattern2.matcher(it)
                            matcher.find()
                            startWeek = matcher.group(1).toInt()
                            endWeek = startWeek
                        }


                        val flag = isContainName(baseList, courseName)
                        if (flag == -1) {
                            baseList.add(CourseBaseBean(id, courseName,
                                    "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}",
                                    importId))
                            detailList.add(CourseDetailBean(
                                    id = id, room = room,
                                    teacher = teacher, day = countDay,
                                    step = step, startWeek = startWeek, endWeek = endWeek,
                                    type = type, startNode = node,
                                    tableId = importId
                            ))
                            id++
                        } else {
                            detailList.add(CourseDetailBean(
                                    id = flag, room = room,
                                    teacher = teacher, day = countDay,
                                    step = step, startWeek = startWeek, endWeek = endWeek,
                                    type = type, startNode = node,
                                    tableId = importId
                            ))
                        }
                    }
                }
                countDay++
            }
        }
        return write2DB()
    }

    suspend fun loginShanghai(number: String, psd: String, port: Int): String {
        baseList.clear()
        detailList.clear()
        val course = ArrayList<String>()
        val connect =
                if (port == 0) {
                    Jsoup.connect("https://oauth.shu.edu.cn/oauth/authorize?response_type=code&client_id=yRQLJfUsx326fSeKNUCtooKw&redirect_uri=http%3a%2f%2fxk.autoisp.shu.edu.cn%2fpassport%2freturn")
                } else {
                    Jsoup.connect("https://oauth.shu.edu.cn/oauth/authorize?response_type=code&client_id=yRQLJfUsx326fSeKNUCtooKw&redirect_uri=http%3a%2f%2fxk.autoisp.shu.edu.cn%3a8080%2fpassport%2freturn")
                }
        var doc = connect.get()

        var ele = doc.body().select("input[name]")

        val res = Jsoup.connect("https://sso.shu.edu.cn/idp/profile/SAML2/POST/SSO")
                .data("SAMLRequest", ele[0].attr("value"), "RelayState", ele[1].attr("value"))
                .method(Connection.Method.POST).timeout(10000).execute()
        //.followRedirects(false)
        val userPassword = HashMap<String, String>()
        userPassword["j_username"] = number
        userPassword["j_password"] = psd

        doc = Jsoup.connect("https://sso.shu.edu.cn/idp/Authn/UserPassword")
                .data(userPassword)
                .cookies(res.cookies())
                .post()
        ele = doc.body().select("input[name]")

        val res2 = Jsoup.connect("http://oauth.shu.edu.cn/oauth/Shibboleth.sso/SAML2/POST")
                .data("SAMLResponse", ele[1].attr("value"), "RelayState", ele[0].attr("value"))
                .method(Connection.Method.POST).timeout(10000).execute()

//        doc = Jsoup.connect("http://xk.autoisp.shu.edu.cn/StudentQuery/CtrlViewQueryCourseTable")
//                .data("studentNo", number)
//                .cookies(res2.cookies())
//                .post()

        doc = Jsoup.connect(
                if (port == 0) {
                    "http://xk.autoisp.shu.edu.cn/StudentQuery/CtrlViewQueryCourseTable"
                } else {
                    "http://xk.autoisp.shu.edu.cn:8080/StudentQuery/CtrlViewQueryCourseTable"
                })
                .data("studentNo", number)
                .cookies(res2.cookies())
                .post()
        val ele2 = doc.body().select("tr")
        for (i in 3 until ele2.size) {
            if (ele2[i].getElementsByTag("td").text().isBlank()) {
                break
            }
            course.add(ele2[i].getElementsByTag("td").text())
        }

        fun getInformation(info: String): List<String> {
            val strList = listOf(*info.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            val list = ArrayList<String>()
            list.add(strList[1])
            list.add(strList[2])
            list.add(strList[4])
            list.add(strList[strList.size - 4])
            val regex = "[一二三四五六七日][0-9]+-[0-9]+"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(info)
            var courseTime: MutableList<String> = ArrayList()
            while (matcher.find()) {
                courseTime.add(matcher.group())
            }
            courseTime = courseTime.subList(0, courseTime.size - 1)
            list.addAll(courseTime)
            return list
        }

        for (i in 0 until course.size - 1) {
            val list = getInformation(course[i])
            val id = baseList.size
            baseList.add(CourseBaseBean(
                    id = id, courseName = list[1],
                    color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), baseList.size % 9))}",
                    tableId = importId
            ))
            for (j in 4 until list.size) {
                val day = getNodeInt(list[j][0].toString())
                val startNode = list[j].substring(1, list[j].indexOf("-")).toInt()
                val endNode = list[j].substring(list[j].indexOf("-") + 1).toInt()
                val type = when {
                    list[j].contains('单') -> 1
                    list[j].contains('双') -> 2
                    else -> 0
                }
                detailList.add(CourseDetailBean(
                        id = id, day = day, room = list[3], teacher = list[2],
                        startWeek = 1, endWeek = 10, startNode = startNode,
                        step = endNode - startNode + 1,
                        type = type, tableId = importId
                ))
            }
        }
        return write2DB()
    }

    suspend fun convertJLU(courseJSON: JSONObject): String {

        baseList.clear()
        detailList.clear()

        val coursesJsonArray = courseJSON.getJSONArray("value")

        var teachClassMaster: JSONObject
        var lessonTeachers: JSONArray
        var lessonSegment: JSONObject
        var lessonSchedules: JSONArray
        var timeBlock: JSONObject
        var classroom: JSONObject
        var teacherName: String
        var courName: String

        var classSet: Int
        var dayOfWeek: Int
        var beginWeek: Int
        var endWeek: Int
        var nodeTime: Array<Int>
        var weekOddEven = ""
        var classroomName = ""

        for (i in 0 until coursesJsonArray.length()) {

            teachClassMaster = coursesJsonArray.getJSONObject(i).getJSONObject("teachClassMaster")

            lessonSegment = teachClassMaster.getJSONObject("lessonSegment")
            lessonSchedules = teachClassMaster.getJSONArray("lessonSchedules")
            lessonTeachers = teachClassMaster.getJSONArray("lessonTeachers")

            courName = lessonSegment.getString("fullName")

            teacherName = lessonTeachers.getJSONObject(0).getJSONObject("teacher").getString("name")

            val course = CourseBaseBean(
                    id = lessonSegment.getInt("lssgId"),
                    courseName = courName,
                    color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), baseList.size % 9))}",
                    tableId = importId
            )

            for (j in 0 until lessonSchedules.length()) {

                timeBlock = lessonSchedules.getJSONObject(j).getJSONObject("timeBlock")
                classroom = lessonSchedules.getJSONObject(j).getJSONObject("classroom")
                classroomName = classroom.getString("fullName")

                classSet = timeBlock.getInt("classSet")
                dayOfWeek = timeBlock.getInt("dayOfWeek")
                beginWeek = timeBlock.getInt("beginWeek")
                endWeek = timeBlock.getInt("endWeek")

                weekOddEven = try {
                    timeBlock.getString("weekOddEven")
                } catch (e: Exception) {
                    ""
                }

                nodeTime = mathStartEnd(classSet)

                val detail = CourseDetailBean(
                        id = lessonSegment.getInt("lssgId"),
                        teacher = teacherName,
                        startWeek = beginWeek,
                        endWeek = endWeek,
                        room = classroomName,
                        day = dayOfWeek,
                        startNode = nodeTime[0],
                        step = nodeTime[1] - nodeTime[0] + 1,
                        tableId = importId,
                        type =
                        when (weekOddEven.toUpperCase()) {
                            "O" -> 1
                            "E" -> 2
                            else -> 0
                        }
                )
                detailList.add(detail)
            }
            baseList.add(course)
        }
        return write2DB()
    }

    fun convertHUST(courseHTML: String): String {
        val doc = Jsoup.parse(courseHTML)

        val ktlist = doc.getElementById("ktlist")
        val lis = doc.select("li:has(p)")

        val teachers = HashMap<String, ArrayList<String>>()
        val hashMapDay = hashMapOf("一" to 1, "二" to 2, "三" to 3, "四" to 4, "五" to 5, "六" to 6, "天" to 7, "日" to 7)


        val hashMapCourse = HashMap<String, ArrayList<CourseDetailBean>>()

        for ((courseId, i) in lis.withIndex()) {

            var courseName: String
            var ps: Elements
            var textTeacher: String
            try {
                courseName = i.selectFirst("strong").html().trim()
                ps = i.select("p")
                textTeacher = ps[0].html().replace(" ", "").split("：").lastOrNull() ?: ""
            } catch (e: Exception) {
                continue
            }

            val course = CourseBaseBean(
                    id = courseId,
                    courseName = courseName,
                    color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), baseList.size % 9))}",
                    tableId = importId
            )

            baseList.add(course)



            if (hashMapCourse.containsKey(courseName)) {
                hashMapCourse[courseName]!!.forEach { if (!it.teacher!!.split(',').contains(textTeacher)) it.teacher = "${it.teacher},$textTeacher" }
                continue
            }

            hashMapCourse[courseName] = ArrayList()

            // 周次、星期、节次、地点
            val segments = i.select("div[class=\"grid demo-grid\"]:has(div[class=\"col-0\"])")
            for (segment in segments) {
                var infos: List<String>
                var nodes: List<Int>
                var week: List<Int>

                try {
                    infos = segment.select("div[class~=col-]").map { it.html().trim() }
                    nodes = infos[2].substring(1, infos[2].length - 1).split('-').map { it.toInt() }
                    week = infos[0].split('-').map { it.toInt() }

                    assert(infos.count() == 4 && nodes.count() == 2 && week.count() == 2)
                } catch (e: Exception) {
                    continue
                }

                val detail = CourseDetailBean(
                        id = courseId,
                        teacher = textTeacher,
                        startWeek = week[0],
                        endWeek = week[1],
                        room = infos[3],
                        day = hashMapDay[infos[1].substring("星期".length, infos[1].length)]!!,
                        startNode = nodes[0],
                        step = nodes[1] - nodes[0] + 1,
                        tableId = importId,
                        type = 0
                )

                hashMapCourse[courseName]!!.add(detail)
                detailList.add(detail)
            }

        }



        return write2DB()
    }

    private val nodeHashMap = SparseArray<Array<Int>?>()

    private fun mathStartEnd(lessonValue: Int): Array<Int> {
        if (nodeHashMap.get(lessonValue) != null) {
            return nodeHashMap.get(lessonValue)!!
        }
        var now: Int
        for (s in 1..11) {
            for (e in s..11) {
                now = getIndexSum(s, e)
                nodeHashMap.put(now, arrayOf(s, e))
                if (now == lessonValue) {
                    return arrayOf(s, e)
                }
            }
        }
        return arrayOf(12, 12)
    }

    private fun getIndexSum(start: Int, end: Int): Int {
        var value = 0
        for (i in start..end) {
            value += (1 shl i)
        }
        return value
    }

    suspend fun login(id: String, pwd: String, code: String): String {
        val response = importService.login(
                xh = id, pwd = pwd, code = code,
                b = "", view_state = viewStateLoginCode,
                cookies = loginCookieStr
        ).execute()
        if (response.isSuccessful) {
            val result = response.body()?.string()
            if (result != null) {
                return result
            } else {
                throw Exception("error")
            }
        } else {
            throw Exception("error")
        }
    }

    suspend fun getPrepare(id: String): String {
        val response = importService.getPrepare(
                xh = id, referer = "http://xk.suda.edu.cn/xskbcx.aspx?xh=$id",
                cookies = loginCookieStr
        ).execute()
        if (response.isSuccessful) {
            val result = response.body()?.string()
            if (result != null) {
                selectedSchedule = result
                viewStatePostCode = parseViewStateCode(result)
                return result
            } else {
                throw Exception("error")
            }
        } else {
            throw Exception("error")
        }
    }

    suspend fun toSchedule(id: String, name: String, year: String, term: String): String {
        val response = importService.getSchedule(
                xh = id, name = URLEncoder.encode(name, "gb2312"), gnmkdm = "N121603",
                event_target = "xnd",
                event_argument = "",
                view_state = viewStatePostCode,
                cookies = loginCookieStr,
                referer = "http://xk.suda.edu.cn/xskbcx.aspx?xh=" + id + "&xm=" + URLEncoder.encode(name, "gb2312") + "&gnmkdm=N121603",
                xnd = year,
                xqd = term
        ).execute()
        if (response.isSuccessful) {
            val result = response.body()?.string()
            if (result != null) {
                return result
            } else {
                throw Exception("error")
            }
        } else {
            throw Exception("error")
        }
    }

    suspend fun postHtml(school: String, type: String, html: String, qq: String): String {
        val response = MyRetrofitUtils.instance.getService().postHtml(school, type, html, qq).execute()
        if (response.isSuccessful) {
            if (response.body()?.string() == "OK") {
                return "ok"
            } else {
                throw Exception(response.message())
            }
        } else {
            throw Exception(response.message())
        }
    }

    fun parseYears(html: String): List<String>? {
        val selected = "selected"
        val option = "option"

        val doc = Jsoup.parse(html)
        val years = arrayListOf<String>()

        val selects = doc.getElementsByTag("select")
        if (selects == null || selects.size < 2) {
            return null
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

        Log.d("解析", years.toString())
        return years
    }

    fun parseName(html: String): String {
        val start = html.indexOf(">姓名：")
        return html.substring(start + 4, html.indexOf("</span>", start))
    }

    fun html2ImportBean(html: String): ArrayList<ImportBean> {
        val doc = org.jsoup.Jsoup.parse(html)
        val table1 = doc.getElementById("Table1")
        val trs = table1.getElementsByTag("tr")
        val courses = ArrayList<ImportBean>()
        var node = 0
        for (tr in trs) {
            val tds = tr.getElementsByTag("td")
            var countFlag = false
            var countDay = 0
            for (td in tds) {
                val courseSource = td.text().trim()
                if (courseSource.length <= 1) {
                    if (countFlag) {
                        countDay++
                    }
                    continue
                }
                if (Pattern.matches(pattern, courseSource)) {
                    //node number
                    val nodeStr = courseSource.substring(1, courseSource.length - 1)
                    try {
                        node = nodeStr.toInt()
                    } catch (e: Exception) {
                        node = getNodeInt(nodeStr)
                        e.printStackTrace()
                    }
                    countFlag = true
                    continue
                }

                if (inArray(other, courseSource)) {
                    //other list
                    continue
                }

                countDay++
                when (zfType) {
                    0 -> courses.addAll(parseImportBean(countDay, td.html(), node))
                    1 -> courses.addAll(parseImportBean1(countDay, courseSource, node))
                }
                //parseTextInfo(courseSource, node)
            }
        }
        return courses
    }

    private fun parseImportBean(cDay: Int, html: String, node: Int): ArrayList<ImportBean> {
        val courses = ArrayList<ImportBean>()
        var isAbnormal = false
        val courseSplits = if (html.substringBeforeLast("</td>").contains("<br><br><br>")) {
            isAbnormal = true
            html.substringBeforeLast("</td>").split("<br><br><br>")
        } else {
            html.substringBeforeLast("</td>").split("<br><br>")
        }
        for (courseStr in courseSplits) {
            val split = courseStr.substringAfter("\">").substringBeforeLast("</a>").split("<br>")
            if (split.isEmpty() || split.size < 3) continue
            val temp = if (split[1] in courseProperty) {
                if (split.size == 4) {
                    ImportBean(startNode = node, name = split[0],
                            timeInfo = split[2],
                            room = split[3], teacher = "", cDay = cDay)
                } else {
                    ImportBean(startNode = node, name = split[0],
                            timeInfo = split[2],
                            room = split[4], teacher = split[3], cDay = cDay)
                }
            } else {
                if (split.size == 3) {
                    if (!isAbnormal) {
                        ImportBean(startNode = node, name = split[0],
                                timeInfo = split[1],
                                room = split[2], teacher = "", cDay = cDay)
                    } else {
                        ImportBean(startNode = node, name = split[0],
                                timeInfo = split[1],
                                room = "", teacher = split[2], cDay = cDay)
                    }
                } else {
                    ImportBean(startNode = node, name = split[0],
                            timeInfo = split[1],
                            room = split[3], teacher = split[2], cDay = cDay)
                }
            }
            courses.add(temp)
        }
        return courses
    }

    private fun parseImportBean1(cDay: Int, source: String, node: Int): ArrayList<ImportBean> {
        val courses = ArrayList<ImportBean>()
        val split = source.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var preIndex = -1
        for (i in 0 until split.size) {
            if (split[i].contains('{') && split[i].contains('}')) {
                if (preIndex != -1) {
                    if (split[preIndex - 1] in courseProperty) {
                        hasTypeFlag = true
                    }
                    val temp = ImportBean(startNode = node, name = if (hasTypeFlag && preIndex >= 2) split[preIndex - 2] else split[preIndex - 1],
                            timeInfo = split[preIndex],
                            room = "", teacher = "", cDay = cDay)
                    if ((i - preIndex - 2) == 1) {
                        temp.teacher = split[preIndex + 1]
                    } else {
                        temp.teacher = split[preIndex + 1]
                        temp.room = split[preIndex + 2]
                    }
                    courses.add(temp)
                    preIndex = i
                } else {
                    preIndex = i
                }
            }
            if (i == split.size - 1) {
                if (split[preIndex - 1] in courseProperty) {
                    hasTypeFlag = true
                }
                val temp = ImportBean(startNode = node, name = if (hasTypeFlag && preIndex >= 2) split[preIndex - 2] else split[preIndex - 1],
                        timeInfo = split[preIndex],
                        room = "", teacher = "", cDay = cDay)
                if ((i - preIndex) == 1) {
                    temp.teacher = split[preIndex + 1]
                } else {
                    temp.teacher = split[preIndex + 1]
                    temp.room = split[preIndex + 2]
                }
                courses.add(temp)
            }
        }
        return courses
    }

    fun parsePeking(html: String): String {
        baseList.clear()
        detailList.clear()
        val doc = org.jsoup.Jsoup.parse(html)
        val kbtable = doc.select("table[class=datagrid]").first()
        val tBody = kbtable.getElementsByTag("tbody").first()
        var teacher = ""
        for (tr in tBody.getElementsByTag("tr")) {
            val tds = tr.getElementsByTag("td")
            if (tds.size >= 11) {
                if (tds[8].text().contains('未'))
                    continue
                val id = baseList.size
                baseList.add(CourseBaseBean(
                        id = id, courseName = tds[0].text().trim(),
                        color = "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), baseList.size % 9))}",
                        tableId = importId
                ))
                teacher = tds[4].text().trim()
                val timeInfos = tds[7].html().split("<br>")
                var startWeek = 1
                var endWeek = 16
                var startNode = 1
                var endNode = 2
                var type = 0
                var day = 7
                timeInfos.forEach {
                    val timeInfo = Jsoup.parse(it).text().trim().split(' ')
                    if (timeInfo.size >= 2) {
                        if (timeInfo[0].contains('~')) {
                            startWeek = timeInfo[0].substringBefore('~').toInt()
                            endWeek = timeInfo[0].substringAfter('~').substringBefore('周').toInt()
                        }
                        type = when {
                            timeInfo[1].contains('单') -> 1
                            timeInfo[1].contains('双') -> 2
                            else -> 0
                        }
                        WEEK.forEachIndexed { index, s ->
                            if (index != 0) {
                                if (timeInfo[1].contains(s)) {
                                    day = index
                                    return@forEachIndexed
                                }
                            }
                        }
                        val matcher = nodePattern1.matcher(timeInfo[1])
                        if (matcher.find()) {
                            val m = matcher.group(0)
                            startNode = m.substringBefore('~').toInt()
                            endNode = m.substringAfter('~').substringBefore('节').toInt()
                        }
                        val room = if (timeInfo.size >= 3) {
                            timeInfo[2]
                        } else {
                            timeInfo[1].substringAfter('(').substringBefore(')')
                        }
                        detailList.add(CourseDetailBean(
                                id = id, day = day, room = room,
                                teacher = teacher, startNode = startNode,
                                step = endNode - startNode + 1,
                                startWeek = startWeek, endWeek = endWeek,
                                type = type, tableId = importId
                        ))
                    }
                }
            }
        }
        return write2DB()
    }

    suspend fun parseURP(html: String): String {
        baseList.clear()
        detailList.clear()
        val doc = Jsoup.parse(html)

        var kbtables = doc.getElementsByAttributeValue("class", "displayTag")
        try {
            kbtables.last().getElementsByTag("tbody").first()
        } catch (e: Exception) {
            kbtables = doc.getElementsByAttributeValue("class", "table table-striped table-bordered")
        }
        var nameIndex = -1
        var teacherIndex = -1
        var weekIndex = -1
        var dayIndex = -1
        var nodeIndex = -1
        var stepIndex = -1
        var buildingIndex = -1
        var roomIndex = -1
        var step = 1

        fun getDay(dayE: Element): Int {
            val str = dayE.text().trim()
            return try {
                str.toInt()
            } catch (e: Exception) {
                getNodeInt(str)
            }
        }

        fun getStartNode(nodeE: Element): Int {
            return if (nodeE.text().contains('-')) {
                val start = nodeE.text().trim().substringBefore('-').toInt()
                step = nodeE.text().trim().substringAfter('-').substringBefore('节').toInt() - start + 1
                start
            } else {
                try {
                    nodeE.text().trim().substringAfter('第').substringBefore('大').substringBefore('小').toInt()
                } catch (e: Exception) {
                    getNodeInt(nodeE.text().trim().substringAfter('第').substringBefore('大').substringBefore('小'))
                }
            }
        }

        fun getStep(str: String): Int {
            return try {
                str.toInt()
            } catch (e: Exception) {
                getNodeInt(str)
            }
        }

        kbtables.forEach { kbtable ->
            if (kbtable.text().contains("星期一")) return@forEach
            val head = kbtable.getElementsByTag("thead").first()
            val headSize = head.getElementsByTag("th").size

            head.getElementsByTag("th").eachText().forEachIndexed { index, s ->
                when (s.trim()) {
                    "课程名" -> nameIndex = index
                    "教师" -> teacherIndex = index
                    "周次" -> weekIndex = index
                    "星期" -> dayIndex = index
                    "节次" -> nodeIndex = index
                    "节数" -> stepIndex = index
                    "教学楼" -> buildingIndex = index
                    "教室" -> roomIndex = index
                }
            }
            if (dayIndex == -1) return@forEach
            val tBody = kbtable.getElementsByTag("tbody").first()
            var teacher = ""
            for (tr in tBody.getElementsByTag("tr")) {
                val tds = tr.getElementsByTag("td")
                val wholeFlag = tds.size > headSize - weekIndex
                if (tds[if (wholeFlag) dayIndex else dayIndex - weekIndex].text().trim().isNotBlank()) {
                    if (wholeFlag) {
                        if (tds[dayIndex].text().trim().isNotBlank()) {
                            teacher = tds[teacherIndex].text().trim()
                            baseList.add(CourseBaseBean(baseList.size, tds[nameIndex].text(), "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), baseList.size % 9))}", importId))
                        }
                    }
                    val weekStr = tds[if (wholeFlag) weekIndex else 0].text().trim()
                    var startWeek = 1
                    var endWeek = 20
                    if (weekStr.contains(',') && !weekStr.contains('-')) {
                        val weekList = arrayListOf<Int>()
                        val weekStrList = weekStr.split(',')
                        weekStrList.forEachIndexed { index, s ->
                            if (index != weekStrList.size - 1) {
                                weekList.add(s.substringBefore('周').toInt())
                            } else {
                                weekList.add(s.substringBefore('周').toInt())
                            }
                        }
                        weekList.sort()
                        intList2WeekBeanList(weekList).forEach { weekBean ->
                            detailList.add(CourseDetailBean(
                                    day = getDay(tds[if (wholeFlag) dayIndex else dayIndex - weekIndex]),
                                    teacher = teacher,
                                    room = try {
                                        tds[if (wholeFlag) buildingIndex else buildingIndex - weekIndex].text().trim() + tds[if (wholeFlag) roomIndex else roomIndex - weekIndex].text().trim()
                                    } catch (e: Exception) {
                                        ""
                                    },
                                    startNode = getStartNode(tds[if (wholeFlag) nodeIndex else nodeIndex - weekIndex]),
                                    step = if (stepIndex != -1) {
                                        getStep(tds[if (wholeFlag) stepIndex else stepIndex - weekIndex].text().trim())
                                    } else {
                                        step
                                    },
                                    startWeek = weekBean.start, endWeek = weekBean.end, type = weekBean.type,
                                    id = baseList.size - 1, tableId = importId
                            ))
                        }
                    } else {
                        weekStr.split(',').forEach { week ->
                            val matcher = weekPattern1.matcher(week)
                            if (matcher.find()) {
                                println(matcher.group(0))
                                val temp = matcher.group(0).split('-')
                                if (temp.size == 1) {
                                    startWeek = temp[0].toInt()
                                    endWeek = temp[0].toInt()
                                } else {
                                    startWeek = temp[0].toInt()
                                    endWeek = temp[1].toInt()
                                }
                            }
                            val type = when {
                                week.contains('单') -> 1
                                week.contains('双') -> 2
                                else -> 0
                            }
                            detailList.add(CourseDetailBean(
                                    day = getDay(tds[if (wholeFlag) dayIndex else dayIndex - weekIndex]),
                                    teacher = teacher,
                                    room = try {
                                        tds[if (wholeFlag) buildingIndex else buildingIndex - weekIndex].text().trim() + tds[if (wholeFlag) roomIndex else roomIndex - weekIndex].text().trim()
                                    } catch (e: Exception) {
                                        ""
                                    },
                                    startNode = getStartNode(tds[if (wholeFlag) nodeIndex else nodeIndex - weekIndex]),
                                    step = if (stepIndex != -1) {
                                        getStep(tds[if (wholeFlag) stepIndex else stepIndex - weekIndex].text().trim())
                                    } else {
                                        step
                                    },
                                    startWeek = startWeek, endWeek = endWeek, type = type,
                                    id = baseList.size - 1, tableId = importId
                            ))
                        }
                    }
                }
            }
        }

        return write2DB()
    }

    suspend fun parseGuangGong(html: String): String {
        baseList.clear()
        detailList.clear()
        val json = html.substringAfter("var kbxx = ").substringBefore(';')
        val gson = Gson()
        var id = 0
        val weekList = arrayListOf<Int>()
        gson.fromJson<List<GuangBean>>(json, object : TypeToken<List<GuangBean>>() {}.type).forEach {
            weekList.clear()
            it.zcs.split(',').forEach { str ->
                weekList.add(str.toInt())
            }
            weekList.sort()
            val day = it.xq.toInt()
            val startNode = it.jcdm2.split(',')[0].toInt()
            val endNode = if (it.jcdm2.contains(',')) it.jcdm2.split(',').last().toInt() else it.jcdm2.split(',')[0].toInt()
            val step = endNode - startNode + 1
            intList2WeekBeanList(weekList).forEach { weekBean ->
                val flag = isContainName(baseList, it.kcmc)
                if (flag == -1) {
                    id = baseList.size
                    baseList.add(CourseBaseBean(id, it.kcmc, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                    detailList.add(CourseDetailBean(
                            id = id, room = it.jxcdmcs,
                            teacher = it.teaxms, day = day,
                            step = step,
                            startWeek = weekBean.start, endWeek = weekBean.end,
                            type = weekBean.type, startNode = startNode,
                            tableId = importId
                    ))
                } else {
                    detailList.add(CourseDetailBean(
                            id = flag, room = it.jxcdmcs,
                            teacher = it.teaxms, day = day,
                            step = step, startWeek = weekBean.start, endWeek = weekBean.end,
                            type = weekBean.type, startNode = startNode,
                            tableId = importId
                    ))
                }
            }
        }
        return write2DB()
    }

    suspend fun importBean2CourseBean(importList: ArrayList<ImportBean>, source: String): String {
        baseList.clear()
        detailList.clear()
        retryList.clear()
        var id = 0
        for (importBean in importList) {
            val flag = isContainName(baseList, importBean.name)
            if (flag == -1) {
                baseList.add(CourseBaseBean(id, importBean.name, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}"
                        , importId))
                val time = parseTime(importBean, importBean.timeInfo, importBean.startNode, source, importBean.name)
                val day = if (importBean.timeInfo.substring(0, 2) in WEEK) time[0] else importBean.cDay
                detailList.add(CourseDetailBean(
                        id = id, room = importBean.room,
                        teacher = importBean.teacher, day = day,
                        step = time[1], startWeek = time[2], endWeek = time[3],
                        type = time[4], startNode = importBean.startNode,
                        tableId = importId
                ))
                if (day == 0) {
                    retryList.add(importList.size - 1)
                }
                id++
            } else {
                val time = parseTime(importBean, importBean.timeInfo, importBean.startNode, source, importBean.name)
                val day = if (importBean.timeInfo.substring(0, 2) in WEEK) time[0] else importBean.cDay
                detailList.add(CourseDetailBean(
                        id = flag, room = importBean.room,
                        teacher = importBean.teacher, day = day,
                        step = time[1], startWeek = time[2], endWeek = time[3],
                        type = time[4], startNode = importBean.startNode,
                        tableId = importId
                ))
                if (day == 0) {
                    retryList.add(importList.size - 1)
                }
            }
        }

        if (retryList.isNotEmpty()) {
            //todo: post Html
            throw Exception("解析异常")
        } else {
            return write2DB()
        }
    }

    suspend fun parseNewZF(html: String): String {
        baseList.clear()
        detailList.clear()
        var id = 0

        val doc = org.jsoup.Jsoup.parse(html)

        val table1 = doc.getElementById("table1")
        val trs = table1.getElementsByTag("tr")

        var node = 0
        var day = 0
        var teacher = ""
        var room = ""
        var step = 0
        var startWeek = 0
        var endWeek = 0
        var type = 0
        var timeStr = ""
        for (tr in trs) {
            val nodeStr = tr.getElementsByClass("festival").text()
            if (nodeStr.isEmpty()) {
                continue
            }
            node = nodeStr.toInt()

            val tds = tr.getElementsByTag("td")
            for (td in tds) {
                val divs = td.getElementsByTag("div")
                for (div in divs) {
                    val courseValue = div.text().trim()

                    if (courseValue.length <= 1) {
                        continue
                    }

                    val courseName = div.getElementsByClass("title").text()
                    if (courseName.isEmpty()) {
                        continue
                    }

                    day = td.attr("id")[0].toString().toInt()

                    val pList = div.getElementsByTag("p")
                    val weekList = arrayListOf<String>()
                    pList.forEach {
                        when (it.getElementsByAttribute("title").attr("title")) {
                            "教师" -> teacher = it.getElementsByTag("font").last().text().trim()
                            "上课地点" -> room = it.getElementsByTag("font").last().text().trim()
                            "节/周" -> {
                                timeStr = it.getElementsByTag("font").last().text().trim()
                                val matcher = nodePattern.matcher(timeStr)
                                if (matcher.find()) {
                                    val nodeInfo = matcher.group(0)
                                    val nodes = nodeInfo!!.substring(1, nodeInfo.length - 1).split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                                    if (nodes.isNotEmpty()) {
                                        node = nodes[0].toInt()
                                    }
                                    if (nodes.size > 1) {
                                        val endNode = nodes[1].toInt()
                                        step = endNode - node + 1
                                    }
                                }
                                weekList.clear()
                                weekList.addAll(timeStr.substring(timeStr.indexOf(')') + 1).split(','))
                            }
                        }
                    }

                    weekList.forEach {
                        if (it.contains('-')) {
                            val weeks = it.substring(0, it.indexOf('周')).split('-')
                            if (weeks.isNotEmpty()) {
                                startWeek = weeks[0].toInt()
                            }
                            if (weeks.size > 1) {
                                endWeek = weeks[1].toInt()
                            }

                            type = when {
                                it.contains('单') -> 1
                                it.contains('双') -> 2
                                else -> 0
                            }
                        } else {
                            startWeek = it.substring(0, it.indexOf('周')).toInt()
                            endWeek = it.substring(0, it.indexOf('周')).toInt()
                        }

                        val flag = isContainName(baseList, courseName)
                        if (flag == -1) {
                            baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                            detailList.add(CourseDetailBean(
                                    id = id, room = room,
                                    teacher = teacher, day = day,
                                    step = step, startWeek = startWeek, endWeek = endWeek,
                                    type = type, startNode = node,
                                    tableId = importId
                            ))
                            id++
                        } else {
                            detailList.add(CourseDetailBean(
                                    id = flag, room = room,
                                    teacher = teacher, day = day,
                                    step = step, startWeek = startWeek, endWeek = endWeek,
                                    type = type, startNode = node,
                                    tableId = importId
                            ))
                        }
                    }
                }
            }
        }

        return write2DB()
    }

    suspend fun parseOldQZ1(html: String): String {
        baseList.clear()
        detailList.clear()
        val doc = org.jsoup.Jsoup.parse(html)

        var id = 0

        val kbtable = doc.getElementById("kbtable")
        val trs = kbtable.getElementsByTag("tr")

        for (tr in trs) {
            val tds = tr.getElementsByTag("td")
            if (tds.isEmpty()) {
                continue
            }

            var day = -1

            for (td in tds) {
                day++
                val divs = td.getElementsByTag("div")
                for (div in divs) {
                    if (oldQzType == 0) {
                        if (div.attr("style") != "display: none;" || div.text().isBlank()) continue
                    } else {
                        if (div.attr("style") == "display: none;" || div.text().isBlank()) continue
                    }
                    val split = div.html().split("<br>")
                    var preIndex = -1

                    fun toCourse() {
                        val courseName = Jsoup.parse(split[preIndex - 2]).text().trim()
                        val room = Jsoup.parse(split[preIndex + 1]).text().trim()
                        val teacher = Jsoup.parse(split[preIndex - 1]).text().trim()

                        val timeInfo = Jsoup.parse(split[preIndex]).text().trim().split(",")
                        timeInfo.forEach {
                            val weekStr = it.trim().substringBefore('周')
                            val startWeek = if (weekStr.contains('-')) weekStr.split('-')[0].toInt() else weekStr.toInt()
                            val endWeek = if (weekStr.contains('-')) weekStr.split('-')[1].toInt() else weekStr.toInt()
                            val startNode = div.attr("id").split('-')[0].toInt() * 2 - 1
                            val flag = isContainName(baseList, courseName)
                            if (flag == -1) {
                                id = baseList.size
                                baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                                detailList.add(CourseDetailBean(
                                        id = id, room = room,
                                        teacher = teacher, day = day,
                                        step = 2,
                                        startWeek = startWeek, endWeek = endWeek,
                                        type = 0, startNode = startNode,
                                        tableId = importId
                                ))
                            } else {
                                detailList.add(CourseDetailBean(
                                        id = flag, room = room,
                                        teacher = teacher, day = day,
                                        step = 2,
                                        startWeek = startWeek, endWeek = endWeek,
                                        type = 0, startNode = startNode,
                                        tableId = importId
                                ))
                            }
                        }
                    }

                    for (i in 0 until split.size) {
                        val matcher = weekPattern.matcher(split[i])
                        if (matcher.find()) {
                            preIndex = if (preIndex != -1) {
                                toCourse()
                                i
                            } else {
                                i
                            }
                        }
                        if (i == split.size - 1) {
                            toCourse()
                        }
                    }
                }
            }
        }
        return write2DB()
    }

    suspend fun parseOldQZ(html: String): String {
        baseList.clear()
        detailList.clear()
        val doc = org.jsoup.Jsoup.parse(html)
        var id = 0

        val kbtable = doc.getElementById("kbtable")
        val trs = kbtable.getElementsByTag("tr")

        for (tr in trs) {
            val tds = tr.getElementsByTag("td")
            if (tds.isEmpty()) {
                continue
            }

            var day = -1

            for (td in tds) {
                day++
                val divs = td.getElementsByTag("div")
                for (div in divs) {
                    if (div.attr("style") == "display: none;" || div.text().isBlank()) continue
                    val split = div.html().split("<br>")
                    var preIndex = -1

                    fun toCourse() {
                        val courseName = Jsoup.parse(split[preIndex - 3]).text().trim()
                        val room = Jsoup.parse(split[preIndex + 1]).text().trim()
                        val teacher = Jsoup.parse(split[preIndex - 1]).text().trim()
                        val timeInfo = Jsoup.parse(split[preIndex]).text().trim().split("周[")
                        val startWeek = if (timeInfo[0].contains('-')) timeInfo[0].split('-')[0].toInt() else timeInfo[0].toInt()
                        val endWeek = if (timeInfo[0].contains('-')) timeInfo[0].split('-')[1].toInt() else timeInfo[0].toInt()
                        val startNode = timeInfo[1].split('-')[0].toInt()
                        val endNode = timeInfo[1].split('-')[1].substringBefore('节').toInt()
                        val flag = isContainName(baseList, courseName)
                        if (flag == -1) {
                            id = baseList.size
                            baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", tableId = importId))
                            detailList.add(CourseDetailBean(
                                    id = id, room = room,
                                    teacher = teacher, day = day,
                                    step = endNode - startNode + 1,
                                    startWeek = startWeek, endWeek = endWeek,
                                    type = 0, startNode = startNode,
                                    tableId = importId
                            ))
                        } else {
                            detailList.add(CourseDetailBean(
                                    id = flag, room = room,
                                    teacher = teacher, day = day,
                                    step = endNode - startNode + 1,
                                    startWeek = startWeek, endWeek = endWeek,
                                    type = 0, startNode = startNode,
                                    tableId = importId
                            ))
                        }
                    }

                    for (i in 0 until split.size) {
                        if (split[i].contains('[') && split[i].contains(']') && split[i].contains('节') && split[i].contains('周')) {
                            preIndex = if (preIndex != -1) {
                                toCourse()
                                i
                            } else {
                                i
                            }
                        }
                        if (i == split.size - 1) {
                            toCourse()
                        }
                    }
                }
            }
        }
        return write2DB()
    }

    suspend fun parseHNIU(html: String): String {
        baseList.clear()
        detailList.clear()
        val doc = Jsoup.parse(html, "utf-8")

        val tBody = doc.getElementsByAttributeValue("bordercolordark", "#FFFFFF")[0].getElementsByTag("tbody")[0]
        val trs = tBody.getElementsByTag("tr")

        for (tr in trs) {
            val tds = tr.getElementsByTag("td")
            var day = 0
            for (td in tds) {
                if (td.attr("align") == "center") {
                    continue
                }
                if (td.attr("valign") == "top") {
                    day++
                    val courseSource = td.html().split("<br>")
                    if (courseSource.isEmpty()) continue
                    if (courseSource.size <= 4) {
                        if (courseSource[0].isBlank()) continue
                        convertHNIU(day, courseSource)
                    } else {
                        var startIndex = 1
                        courseSource.forEachIndexed { index, s ->
                            if (s.contains('[') && s.contains(']') && s.contains('周') && s.contains('节')) {
                                if (index - 1 != 0) {
                                    convertHNIU(day, courseSource.subList(startIndex - 1, index - 1))
                                    startIndex = index
                                }
                            }
                            if (index == courseSource.size - 1) {
                                convertHNIU(day, courseSource.subList(startIndex - 1, index))
                            }
                        }
                    }
                }
            }
        }

        return write2DB()
    }

    private fun convertHNIU(day: Int, courseSource: List<String>) {
        var startNode = 0
        var step = 0
        var startWeek = 0
        var endWeek = 0
        var id = 0

        val courseName = courseSource[0].split(' ')[0]
        val teacher = courseSource[1].split(' ')[0]
        val room = if (courseSource.size > 2 && courseSource[2].trim().isNotBlank()) {
            courseSource[2].trim()
        } else {
            val tmp = courseSource[1].split(' ')
            tmp[tmp.size - 1]
        }
        val timeStr = courseSource[1].substringAfter('[').substringBeforeLast('节')
        val weekList = timeStr.split("周][")[0].split(", ", ",")
        val nodeStr = timeStr.split("周][")[1]

        val nodeList = nodeStr.split('-')
        if (nodeList.size == 1) {
            startNode = nodeList[0].toInt()
            step = 1
        } else {
            startNode = nodeList[0].toInt()
            step = nodeList[1].toInt() - startNode + 1
        }

        weekList.forEach {
            if (it.contains('-')) {
                val weeks = it.split('-')
                if (weeks.isNotEmpty()) {
                    startWeek = weeks[0].toInt()
                }
                if (weeks.size > 1) {
                    endWeek = weeks[1].toInt()
                }
            } else {
                startWeek = it.toInt()
                endWeek = it.toInt()
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                detailList.add(CourseDetailBean(
                        id = id, room = room,
                        teacher = teacher, day = day,
                        step = step,
                        startWeek = startWeek, endWeek = endWeek,
                        type = 0, startNode = startNode,
                        tableId = importId
                ))
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = room,
                        teacher = teacher, day = day,
                        step = step, startWeek = startWeek, endWeek = endWeek,
                        type = 0, startNode = startNode,
                        tableId = importId
                ))
            }
        }
    }

    suspend fun parseQZ(html: String, type: String): String {
        baseList.clear()
        detailList.clear()
        val doc = org.jsoup.Jsoup.parse(html, "utf-8")

        val kbtable = doc.getElementById("kbtable")
        val trs = kbtable.getElementsByTag("tr")

        var nodeCount = 0
        for (tr in trs) {
            val tds = tr.getElementsByTag("td")
            if (tds.isEmpty()) {
                continue
            }
            nodeCount++

            var day = 0

            for (td in tds) {
                day++
                val divs = td.getElementsByTag("div")
                for (div in divs) {
                    val courseElements = if (qzType == 6) div.getElementsByClass("kbcontent1") else div.getElementsByClass("kbcontent")
                    if (courseElements.text().isBlank()) {
                        continue
                    }
                    val courseHtml = courseElements.html()
                    var startIndex = 0
                    var splitIndex = courseHtml.indexOf("-----")
                    while (splitIndex != -1) {
                        when (type) {
                            in qzGuangwaiList -> convertGuangWai(day, courseHtml.substring(startIndex, splitIndex))
                            in qzCrazyList -> convertQZMore(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                            in qzMoreNodeSchoolList -> convertQZMore(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                            in qzLessNodeSchoolList -> convertQZLess(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                            in qzAbnormalNodeList -> convertBeijingLinYeDaXue(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                            "长春大学" -> convertChangChunDaXue(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                        }
                        startIndex = courseHtml.indexOf("<br>", splitIndex) + 4
                        splitIndex = courseHtml.indexOf("-----", startIndex)
                    }
                    when (type) {
                        in qzGuangwaiList -> convertGuangWai(day, courseHtml.substring(startIndex, courseHtml.length))
                        in qzCrazyList -> convertQZMore(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                        in qzMoreNodeSchoolList -> convertQZMore(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                        in qzLessNodeSchoolList -> convertQZLess(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                        in qzAbnormalNodeList -> convertBeijingLinYeDaXue(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                        "长春大学" -> convertChangChunDaXue(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                    }
                }
            }
        }
        return write2DB()
    }

    private fun convertQZMore(day: Int, nodeCount: Int, infoStr: String) {
        val node = nodeCount * 2 - 1
        val courseHtml = Jsoup.parse(infoStr)
        val courseName = Jsoup.parse(infoStr.substringBefore("<font").trim()).text()
        val teacher = courseHtml.getElementsByAttributeValue("title", "老师").text().trim()
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim() + courseHtml.getElementsByAttributeValue("title", "分组").text().trim()
        val weekStr = courseHtml.getElementsByAttributeValue("title", "周次(节次)").text()
        val weekList = weekStr.split(',')
        var startWeek = 0
        var endWeek = 0
        var type = 0
        var id = 0
        weekList.forEach {
            if (it.contains('-')) {
                val weeks = it.split('-')
                if (weeks.isNotEmpty()) {
                    startWeek = weeks[0].toInt()
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = weeks[1].substringBefore('(').toInt()
                }
            } else {
                startWeek = it.substringBefore('(').toInt()
                endWeek = it.substringBefore('(').toInt()
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                detailList.add(CourseDetailBean(
                        id = id, room = room,
                        teacher = teacher, day = day,
                        step = 2,
                        startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = node,
                        tableId = importId
                ))
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = room,
                        teacher = teacher, day = day,
                        step = 2, startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = node,
                        tableId = importId
                ))
            }
        }
    }

    private fun convertQZLess(day: Int, nodeCount: Int, infoStr: String) {
        val courseHtml = Jsoup.parse(infoStr)
        val courseName = Jsoup.parse(infoStr.substringBefore("<font").trim()).text()
        val teacher = courseHtml.getElementsByAttributeValue("title", "老师").text().trim()
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim() + courseHtml.getElementsByAttributeValue("title", "分组").text().trim()
        val weekStr = courseHtml.getElementsByAttributeValue("title", "周次(节次)").text()
        val weekList = weekStr.split(',')
        var startWeek = 0
        var endWeek = 0
        var type = 0
        var id = 0
        weekList.forEach {
            if (it.contains('-')) {
                val weeks = it.split('-')
                if (weeks.isNotEmpty()) {
                    startWeek = weeks[0].toInt()
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = weeks[1].substringBefore('(').toInt()
                }
            } else {
                startWeek = it.substringBefore('(').toInt()
                endWeek = it.substringBefore('(').toInt()
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                detailList.add(CourseDetailBean(
                        id = id, room = room,
                        teacher = teacher, day = day,
                        step = 1,
                        startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = nodeCount,
                        tableId = importId
                ))
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = room,
                        teacher = teacher, day = day,
                        step = 1, startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = nodeCount,
                        tableId = importId
                ))
            }
        }
    }

    // 北京林业大学
    private fun convertBeijingLinYeDaXue(day: Int, nodeCount: Int, infoStr: String) {
        val node = if (nodeCount <= 3) {
            nodeCount * 2 - 1
        } else {
            nodeCount * 2 - 2
        }
        val courseHtml = Jsoup.parse(infoStr)
        val courseName = infoStr.substringBefore("<br>").trim()
        val teacher = courseHtml.getElementsByAttributeValue("title", "老师").text().trim()
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim() + courseHtml.getElementsByAttributeValue("title", "分组").text().trim()
        val weekStr = courseHtml.getElementsByAttributeValue("title", "周次(节次)").text().substringBefore("(周)")
        val weekList = weekStr.split(',')
        var startWeek = 0
        var endWeek = 0
        var id = 0
        var type = 0
        weekList.forEach {
            if (it.contains('-')) {
                val weeks = it.split('-')
                if (weeks.isNotEmpty()) {
                    startWeek = weeks[0].toInt()
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = weeks[1].substringBefore('(').toInt()
                }
            } else {
                startWeek = it.substringBefore('(').toInt()
                endWeek = it.substringBefore('(').toInt()
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                detailList.add(CourseDetailBean(
                        id = id, room = room,
                        teacher = teacher, day = day,
                        step = if (node == 5 || node == 12) 1 else 2,
                        startWeek = startWeek, endWeek = endWeek,
                        type = 0, startNode = node,
                        tableId = importId
                ))
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = room,
                        teacher = teacher, day = day,
                        step = if (node == 5 || node == 12) 1 else 2, startWeek = startWeek, endWeek = endWeek,
                        type = 0, startNode = node,
                        tableId = importId
                ))
            }
        }
    }

    private fun convertChangChunDaXue(day: Int, nodeCount: Int, infoStr: String) {
        val node = nodeCount * 2 - 1
        val courseHtml = Jsoup.parse(infoStr)
        val courseName = infoStr.substringBefore("<br>").trim()
        val teacher = courseHtml.getElementsByAttributeValue("title", "老师").text().trim()
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim() + courseHtml.getElementsByAttributeValue("title", "分组").text().trim()
        val weekStr = courseHtml.getElementsByAttributeValue("title", "周次(节次)").text()
        val weekList = weekStr.split(',')
        var startWeek = 0
        var endWeek = 0
        var type = 0
        var id = 0
        weekList.forEach {
            if (it.contains('-')) {
                val weeks = it.split('-')
                if (weeks.isNotEmpty()) {
                    startWeek = weeks[0].toInt()
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = weeks[1].substringBefore('(').toInt()
                }
            } else {
                startWeek = it.substringBefore('(').toInt()
                endWeek = it.substringBefore('(').toInt()
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                detailList.add(CourseDetailBean(
                        id = id, room = room,
                        teacher = teacher, day = day,
                        step = 2,
                        startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = node,
                        tableId = importId
                ))
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = room,
                        teacher = teacher, day = day,
                        step = 2, startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = node,
                        tableId = importId
                ))
            }
        }
    }

    private fun convertGuangWai(day: Int, infoStr: String) {
        val courseHtml = Jsoup.parse(infoStr)
        val courseName = infoStr.substringBefore("<br>").trim()
        val teacher = courseHtml.getElementsByAttributeValue("title", "老师").text().trim()
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim() + courseHtml.getElementsByAttributeValue("title", "分组").text().trim()
        val tempStr = courseHtml.getElementsByAttributeValue("title", "周次(节次)").text()
        val weekStr = when {
            tempStr.contains(' ') -> courseHtml.getElementsByAttributeValue("title", "周次(节次)").text().split(' ')[0]
            tempStr.isBlank() -> courseHtml.getElementsByAttributeValue("title", "周次").text()
            else -> courseHtml.getElementsByAttributeValue("title", "周次(节次)").text().substringBefore(')')
        }
        val nodeList = when {
            tempStr.contains(' ') -> courseHtml.getElementsByAttributeValue("title", "周次(节次)").text().split(' ')[1].removeSurrounding("[", "]").split('-')
            tempStr.isBlank() -> courseHtml.getElementsByAttributeValue("title", "节次").text().substringAfter(')').removeSurrounding("[", "]").split('-')
            else -> courseHtml.getElementsByAttributeValue("title", "周次(节次)").text().substringAfter(')').removeSurrounding("[", "]").split('-')
        }
        val weekList = weekStr.split(',')
        var startWeek = 0
        var endWeek = 0
        var id = 0
        var type = 0
        weekList.forEach {
            if (it.contains('-')) {
                val weeks = it.split('-')
                if (weeks.isNotEmpty()) {
                    startWeek = weeks[0].toInt()
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = weeks[1].substringBefore('(').toInt()
                }
            } else {
                startWeek = it.substringBefore('(').toInt()
                endWeek = it.substringBefore('(').toInt()
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                detailList.add(CourseDetailBean(
                        id = id, room = room,
                        teacher = teacher, day = day,
                        step = nodeList.last().substringBefore('节').toInt() - nodeList.first().substringBefore('节').toInt() + 1,
                        startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = nodeList.first().substringBefore('节').toInt(),
                        tableId = importId
                ))
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = room,
                        teacher = teacher, day = day,
                        step = nodeList.last().substringBefore('节').toInt() - nodeList.first().substringBefore('节').toInt() + 1, startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = nodeList.first().substringBefore('节').toInt(),
                        tableId = importId
                ))
            }
        }
    }

    suspend fun importFromFile(path: String): String {
        val gson = Gson()
        val file = File(path)
        val list = file.readLines()
        val timeTable = gson.fromJson<TimeTableBean>(list[0], object : TypeToken<TimeTableBean>() {}.type)
        val timeDetails = gson.fromJson<List<TimeDetailBean>>(list[1], object : TypeToken<List<TimeDetailBean>>() {}.type)
        val table = gson.fromJson<TableBean>(list[2], object : TypeToken<TableBean>() {}.type)
        val courseBaseList = gson.fromJson<List<CourseBaseBean>>(list[3], object : TypeToken<List<CourseBaseBean>>() {}.type)
        val courseDetailList = gson.fromJson<List<CourseDetailBean>>(list[4], object : TypeToken<List<CourseDetailBean>>() {}.type)
        val timeTableId = timeTableDao.getMaxIdInThread() + 1
        timeTable.id = timeTableId
        timeTable.name = "分享_" + timeTable.name
        timeDetails.forEach {
            it.timeTable = timeTableId
        }
        val tableId = getNewId()
        table.background = ""
        table.id = tableId
        table.timeTable = timeTableId
        table.type = 0
        courseBaseList.forEach {
            it.tableId = tableId
        }
        courseDetailList.forEach {
            it.tableId = tableId
        }
        timeTableDao.insertTimeTable(timeTable)
        timeDetailDao.insertTimeList(timeDetails)
        tableDao.insertTable(table)
        baseDao.insertList(courseBaseList)
        detailDao.insertList(courseDetailList)
        return "ok"
    }

    private fun write2DB(): String {
        if (baseList.isEmpty()) {
            throw Exception("解析错误>_<请确保选择了正确的教务类型，并在显示了课程的页面")
        }
        //todo: 增量添加课程
        if (!newFlag) {
            baseDao.removeCourseBaseBeanOfTable(importId)
        } else {
            tableDao.insertTable(TableBean(id = importId, tableName = "未命名"))
        }
        baseDao.insertList(baseList)
        detailDao.insertList(detailList)
        return "ok"
    }

    private fun parseTime(importBean: ImportBean, time: String, startNode: Int, source: String, courseName: String): Array<Int> {
        val result = Array(5) { 0 }
        //按顺序分别为day, step, startWeek, endWeek, type

        //day
        if (time[0] == '周') {
            val dayStr = time.substring(0, 2)
            val day = getIntWeek(dayStr)
            result[0] = day
        }
        if (result[0] == 0) {
            var startIndex = source.indexOf(">第${startNode}节</td>")
            if (startIndex == -1) {
                startIndex = source.indexOf(">第${CourseUtils.getNodeStr(startNode)}节</td>")
            }
            var endIndex = 0
            if (startIndex != -1) {
                endIndex = source.indexOf(courseName, startIndex)
            }
            if (startIndex != -1 && endIndex != -1) {
                result[0] = countStr(source.substring(startIndex, endIndex), "Center")
            }
        }

        //step
        var step = 0
        when {
            time.contains("节/") -> {
                val numLocate = time.indexOf("节/")
                step = time.substring(numLocate - 1, numLocate).toInt()
            }
            time.contains(",") -> {
                var locate = 0
                step = 1
                while (time.indexOf(",", locate) != -1 && locate < time.length) {
                    step += 1
                    locate = time.indexOf(",", locate) + 1
                }
            }
            time.contains("第${startNode}节") -> {
                step = 1
            }
        }
        if (step == 0) {
            val matcher = nodePattern.matcher(time)
            if (matcher.find()) {
                val nodeInfo = matcher.group(0)
                val nodes = nodeInfo!!.substring(1, nodeInfo.length - 1).split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (nodes.isNotEmpty()) {
                    importBean.startNode = nodes[0].toInt()
                }
                if (nodes.size > 1) {
                    step = nodes[1].toInt() - importBean.startNode + 1
                }
            }
        }
        result[1] = step

        //周数
        var startWeek = 1
        var endWeek = 20
        val matcher = pattern1.matcher(time)
        if (matcher.find()) {
            val weekInfo = matcher.group(0)//第2-16周
            val weeks = weekInfo.substring(2, weekInfo.length - 1).split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

            if (weeks.isNotEmpty()) {
                startWeek = weeks[0].toInt()
                result[2] = startWeek
            }
            if (weeks.size > 1) {
                endWeek = weeks[1].toInt()
                result[3] = endWeek
            }
        }

        //单双周
        if (time.contains("单周")) {
            result[4] = 1
        } else if (time.contains("双周")) {
            result[4] = 2
        }

        return result
    }

    private fun getIntWeek(chinaWeek: String): Int {
        for (i in 0 until WEEK.size) {
            if (WEEK[i] == chinaWeek) {
                return i
            }
        }
        return 0
    }

    private fun inArray(arr: Array<String>, targetValue: String): Boolean {
        for (s in arr) {
            if (s == targetValue)
                return true
        }
        return false
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

    suspend fun importFromExcel(path: String): String {
        baseList.clear()
        detailList.clear()
        var errorFlag = false
        analyzeXlsx(path).values.forEach {
            for (i in 1 until it.size) {
                if (it[i].size != 7) {
                    errorFlag = true
                    continue
                }
                if (it[i][0].isBlank() || it[i][1].isBlank() || it[i][2].isBlank() || it[i][3].isBlank() || it[i][6].isBlank()) {
                    continue
                }
                var startWeek = 0
                var endWeek = 0
                var type = 0
                // Log.d("Excel", it[i][0] + ", " + it[i][1] + ", " + it[i][2] + ", " + it[i][3] + ", " + it[i][4] + ", " + it[i][5] + ", " + it[i][6])
                val weekList = it[i][6].split('、')
                weekList.forEach { weekStr ->
                    if (weekStr.contains('-')) {
                        val weeks = weekStr.split('-')
                        startWeek = weeks[0].toInt()
                        when {
                            weekStr.contains('单') -> {
                                type = 1
                                endWeek = weeks[1].substringBefore('单').toInt()
                            }
                            weekStr.contains('双') -> {
                                type = 2
                                endWeek = weeks[1].substringBefore('双').toInt()
                            }
                            else -> {
                                type = 0
                                endWeek = weeks[1].toInt()
                            }
                        }
                    } else {
                        startWeek = weekStr.toInt()
                        endWeek = weekStr.toInt()
                        type = 0
                    }

                    val startNode = it[i][2].toInt()
                    val endNode = it[i][3].toInt()
                    val flag = isContainName(baseList, it[i][0])
                    if (flag == -1) {
                        val id = baseList.size
                        baseList.add(CourseBaseBean(id, it[i][0], "#${Integer.toHexString(ViewUtils.getCustomizedColor(getApplication(), id % 9))}", importId))
                        detailList.add(CourseDetailBean(
                                id = id, room = it[i][5],
                                teacher = it[i][4], day = it[i][1].toInt(),
                                step = endNode - startNode + 1,
                                startWeek = startWeek, endWeek = endWeek,
                                type = type, startNode = startNode,
                                tableId = importId
                        ))
                    } else {
                        detailList.add(CourseDetailBean(
                                id = flag, room = it[i][5],
                                teacher = it[i][4], day = it[i][1].toInt(),
                                step = endNode - startNode + 1, startWeek = startWeek, endWeek = endWeek,
                                type = type, startNode = startNode,
                                tableId = importId
                        ))
                    }
                }
            }
        }
        write2DB()
        return if (errorFlag) {
            "something"
        } else {
            "ok"
        }
    }

    private val SHAREDSTRINGS = "xl/sharedStrings.xml"
    private val DIRSHEET = "xl/worksheets/"
    private val ENDXML = ".xml"
    private val listCells = ArrayList<String>()

    private fun analyzeXlsx(fileName: String): Map<String, List<List<String>>> {
        val map = HashMap<String, List<List<String>>>()
        var isShareStrings: InputStream? = null
        var isXlsx: InputStream? = null
        var zipInputStream: ZipInputStream? = null
        listCells.clear()
        try {
            val zipFile = ZipFile(File(fileName))
            val sharedStringXML = zipFile.getEntry(SHAREDSTRINGS)//准备xl/sharedStrings.xml文件
            isShareStrings = zipFile.getInputStream(sharedStringXML)
            val xmlPullParser = Xml.newPullParser()//开始解析xl/sharedStrings.xml文件
            xmlPullParser.setInput(isShareStrings, "utf-8")
            var eventType = xmlPullParser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tag = xmlPullParser.name
                        if ("t" == tag) { //如果为 " t " 标签的话将标签中得到元素添加到list集合中
                            listCells.add(xmlPullParser.nextText())
                        }
                    }
                    else -> {
                    }
                }
                eventType = xmlPullParser.next()
            }
            //
            isXlsx = BufferedInputStream(FileInputStream(fileName))   //准备遍历xl/worksheets目录下的sheet.xml文件
            zipInputStream = ZipInputStream(isXlsx)
            var zipDir: ZipEntry? = zipInputStream.nextEntry
            while (zipDir != null) {
                val dirName = zipDir.name
                if (!zipDir.isDirectory && dirName.endsWith(ENDXML)) { // 不是文件夹，且以 ".xml"结尾
                    if (dirName.contains(DIRSHEET)) { //文件名包含 "xl/worksheets/",则为sheet1.xml与sheet2.xml等
                        parseSheet(zipFile, dirName, map)  //开始解析sheet.xml
                    }
                }
                zipDir = zipInputStream.nextEntry
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                zipInputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                isXlsx!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                isShareStrings!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return map
    }

    private fun parseSheet(zipFile: ZipFile, entryName: String, map: MutableMap<String, List<List<String>>>) {
        val lastIndexOf = entryName.lastIndexOf(File.separator)
        val sheetName = entryName.substring(lastIndexOf + 1, entryName.length - 4)//得出map的key值，如: sheet1，sheet2等
        //
        var v: String? = null  //用于存放" v " 标签的值
        var columns: MutableList<String>? = null //用于存放每行的列信息
        val rows = ArrayList<List<String>>() //用于存放每个sheet的行信息
        var inputStreamSheet: InputStream? = null
        try {
            val sheet = zipFile.getEntry(entryName)
            inputStreamSheet = zipFile.getInputStream(sheet)
            val xmlPullParserSheet = Xml.newPullParser()//开始解析
            xmlPullParserSheet.setInput(inputStreamSheet, "utf-8")
            var evenTypeSheet = xmlPullParserSheet.eventType
            while (XmlPullParser.END_DOCUMENT != evenTypeSheet) {
                when (evenTypeSheet) {
                    XmlPullParser.START_TAG -> {
                        val tag = xmlPullParserSheet.name
                        if ("row".equals(tag, ignoreCase = true)) {  //如果是每行的开始标签，则初始化列list
                            columns = ArrayList()
                        } else if ("v".equals(tag, ignoreCase = true)) { //如果是" v "标签则利用得到的索引，得到对应行对应列的元素
                            v = xmlPullParserSheet.nextText()
                            if (v != null) {
                                columns!!.add(listCells[v.toInt()])
                            } else {
                                columns!!.add(v)
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> if ("row".equals(xmlPullParserSheet.name, ignoreCase = true) && v != null) {//一行结束将结构保存在rows中
                        rows.add(columns!!)
                    }
                }
                evenTypeSheet = xmlPullParserSheet.next()
            }
            if (rows.size > 0) { //sheet中内容不为空则保存到map中
                map[sheetName] = rows
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStreamSheet!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}