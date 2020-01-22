package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Application
import android.util.SparseArray
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.*
import com.suda.yzune.wakeupschedule.schedule_import.exception.PasswordErrorException
import com.suda.yzune.wakeupschedule.schedule_import.parser.*
import com.suda.yzune.wakeupschedule.schedule_import.parser.qz.QzBrParser
import com.suda.yzune.wakeupschedule.schedule_import.parser.qz.QzCrazyParser
import com.suda.yzune.wakeupschedule.schedule_import.parser.qz.QzParser
import com.suda.yzune.wakeupschedule.schedule_import.parser.qz.QzWithNodeParser
import com.suda.yzune.wakeupschedule.schedule_import.suda.SudaXK
import com.suda.yzune.wakeupschedule.utils.CourseUtils.getNodeInt
import com.suda.yzune.wakeupschedule.utils.MyRetrofitUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File
import java.util.regex.Pattern


class ImportViewModel(application: Application) : AndroidViewModel(application) {

    var importId = -1
    var school: String? = null
    var importType: String? = null

    var newFlag = false
    var isUrp = false
    var zfType = 0
    var qzType = 0
    var oldQzType = 0
    var htmlName = ""
    var htmlPath = ""

    var sudaXK: SudaXK? = null

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val courseDao = dataBase.courseDao()
    private val timeTableDao = dataBase.timeTableDao()
    private val timeDetailDao = dataBase.timeDetailDao()

    var schoolInfo = Array(3) { "" }

    private val baseList = arrayListOf<CourseBaseBean>()
    private val detailList = arrayListOf<CourseDetailBean>()

    suspend fun importSchedule(source: String): Int {
        val parser = when (importType) {
            Common.TYPE_ZF -> ZhengFangParser(source, 0)
            Common.TYPE_ZF_1 -> ZhengFangParser(source, 1)
            Common.TYPE_ZF_NEW -> NewZFParser(source)
            Common.TYPE_URP -> UrpParser(source)
            Common.TYPE_URP_NEW -> NewUrpParser(source)
            Common.TYPE_QZ -> QzParser(source)
            Common.TYPE_QZ_OLD -> OldQzParser(source)
            Common.TYPE_QZ_CRAZY -> QzCrazyParser(source)
            Common.TYPE_QZ_BR -> QzBrParser(source)
            Common.TYPE_QZ_WITH_NODE -> QzWithNodeParser(source)
            Common.TYPE_CF -> ChengFangParser(source)
            Common.TYPE_PKU -> PekingParser(source)
            Common.TYPE_BNUZ -> BNUZParser(source)
            Common.TYPE_HNIU -> HNIUParser(source)
            Common.TYPE_HNUST -> HNUSTParser(source, oldQzType)
            else -> null
        }
        return parser?.saveCourse(getApplication(), importId) { baseList, detailList ->
            if (!newFlag) {
                courseDao.coverImport(baseList, detailList)
            } else {
                tableDao.insertTable(TableBean(id = importId, tableName = "未命名"))
                courseDao.insertCourses(baseList, detailList)
            }
        } ?: throw Exception("请确保选择正确的教务类型，以及到达显示课程的页面")
    }

    suspend fun getNewId(): Int {
        val lastId = tableDao.getLastId()
        return if (lastId != null) lastId + 1 else 1
    }

    suspend fun loginTsinghua(username: String, password: String): Int {
        baseList.clear()
        detailList.clear()

        val LEARN_PREFIX = "https://learn2018.tsinghua.edu.cn"
        var cookies: Map<String, String>?
        val ticket = withContext(Dispatchers.IO) {
            Jsoup.connect("https://id.tsinghua.edu.cn/do/off/ui/auth/login/post/bb5df85216504820be7bba2b0ae1535b/0?/login.do")
                    .data("i_user", username).data("i_pass", password).data("atOnce", true.toString())
                    .timeout(10000).post()
                    .body().select("a").attr("href").split('=').last()
        }
        val loginResponse = withContext(Dispatchers.IO) {
            Jsoup.connect("$LEARN_PREFIX/b/j_spring_security_thauth_roaming_entry?ticket=$ticket")
                    .execute()
        }.let {
            cookies = it.cookies()
            it.statusCode() in 200..299
        }
        if (!loginResponse) throw PasswordErrorException("Incorrect username or password.")
        //getSemesterIdList
        val semesterIdArray = withContext(Dispatchers.IO) {
            JSONArray(Jsoup.connect("$LEARN_PREFIX/b/wlxt/kc/v_wlkc_xs_xktjb_coassb/queryxnxq")
                    .cookies(cookies).execute().body())
        }.let {
            Array<String>(it.length()) { i: Int -> it.getString(i) }
        }
        //getCurrentSemester
        var currentSemester = withContext(Dispatchers.IO) {
            JSONObject(Jsoup.connect("$LEARN_PREFIX/b/kc/zhjw_v_code_xnxq/getCurrentAndNextSemester")
                    .cookies(cookies).execute().body())
                    .getJSONObject("result").getString("id")
        }
        if (currentSemester.split("-").last() == "3" && semesterIdArray.indexOf(currentSemester) > 0)
            currentSemester = semesterIdArray[semesterIdArray.indexOf(currentSemester) - 1]
        //getCourseList
        val courseList = withContext(Dispatchers.IO) {
            JSONObject(Jsoup.connect("$LEARN_PREFIX/b/wlxt/kc/v_wlkc_xs_xkb_kcb_extend/student/loadCourseBySemesterId/$currentSemester")
                    .cookies(cookies).execute().body())
                    .getJSONArray("resultList")
        }
        val courseDetailList = withContext(Dispatchers.IO) {
            Array<Array<String>>(courseList.length()) { i ->
                JSONArray(Jsoup.connect("$LEARN_PREFIX/b/kc/v_wlkc_xk_sjddb/detail?id=${courseList.getJSONObject(i).getString("wlkcid")}")
                        .cookies(cookies).execute().body()).let {
                    Array(it.length()) { idx ->
                        it.getString(idx)
                    }
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

    suspend fun loginShanghai(number: String, psd: String, port: Int): Int {
        baseList.clear()
        detailList.clear()

        val course = ArrayList<String>()
        val connect =
                if (port == 0) {
                    Jsoup.connect("https://oauth.shu.edu.cn/oauth/authorize?response_type=code&client_id=yRQLJfUsx326fSeKNUCtooKw&redirect_uri=http%3a%2f%2fxk.autoisp.shu.edu.cn%2fpassport%2freturn")
                } else {
                    Jsoup.connect("https://oauth.shu.edu.cn/oauth/authorize?response_type=code&client_id=yRQLJfUsx326fSeKNUCtooKw&redirect_uri=http%3a%2f%2fxk.autoisp.shu.edu.cn%3a8080%2fpassport%2freturn")
                }
        var doc = withContext(Dispatchers.IO) { connect.get() }

        var ele = doc.body().select("input[name]")

        val res = withContext(Dispatchers.IO) {
            Jsoup.connect("https://sso.shu.edu.cn/idp/profile/SAML2/POST/SSO")
                    .data("SAMLRequest", ele[0].attr("value"), "RelayState", ele[1].attr("value"))
                    .method(Connection.Method.POST).timeout(10000).execute()
        }
        //.followRedirects(false)
        val userPassword = HashMap<String, String>()
        userPassword["j_username"] = number
        userPassword["j_password"] = psd

        doc = withContext(Dispatchers.IO) {
            Jsoup.connect("https://sso.shu.edu.cn/idp/Authn/UserPassword")
                    .data(userPassword)
                    .cookies(res.cookies())
                    .post()
        }
        ele = doc.body().select("input[name]")

        val res2 = withContext(Dispatchers.IO) {
            Jsoup.connect("http://oauth.shu.edu.cn/oauth/Shibboleth.sso/SAML2/POST")
                    .data("SAMLResponse", ele[1].attr("value"), "RelayState", ele[0].attr("value"))
                    .method(Connection.Method.POST).timeout(10000).execute()
        }

//        doc = Jsoup.connect("http://xk.autoisp.shu.edu.cn/StudentQuery/CtrlViewQueryCourseTable")
//                .data("studentNo", number)
//                .cookies(res2.cookies())
//                .post()

        doc = withContext(Dispatchers.IO) {
            Jsoup.connect(
                    if (port == 0) {
                        "http://xk.autoisp.shu.edu.cn/StudentQuery/CtrlViewQueryCourseTable"
                    } else {
                        "http://xk.autoisp.shu.edu.cn:8080/StudentQuery/CtrlViewQueryCourseTable"
                    })
                    .data("studentNo", number)
                    .cookies(res2.cookies())
                    .post()
        }

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

    suspend fun convertJLU(courseJSON: JSONObject): Int {

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

    suspend fun convertHUST(courseHTML: String): Int {
        baseList.clear()
        detailList.clear()

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

    suspend fun importFromFile(path: String): String {
        val gson = Gson()
        val file = File(path)
        val list = file.readLines()
        val timeTable = gson.fromJson<TimeTableBean>(list[0], object : TypeToken<TimeTableBean>() {}.type)
        val timeDetails = gson.fromJson<List<TimeDetailBean>>(list[1], object : TypeToken<List<TimeDetailBean>>() {}.type)
        val table = gson.fromJson<TableBean>(list[2], object : TypeToken<TableBean>() {}.type)
        val courseBaseList = gson.fromJson<List<CourseBaseBean>>(list[3], object : TypeToken<List<CourseBaseBean>>() {}.type)
        val courseDetailList = gson.fromJson<List<CourseDetailBean>>(list[4], object : TypeToken<List<CourseDetailBean>>() {}.type)
        val timeTableId = timeTableDao.getMaxId() + 1
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
        courseDao.insertCourses(courseBaseList, courseDetailList)
        return "ok"
    }

    private suspend fun write2DB(): Int {
        if (baseList.isEmpty()) {
            throw Exception("解析错误>_<请确保选择了正确的教务类型，并在显示了课程的页面")
        }
        if (!newFlag) {
            courseDao.coverImport(baseList, detailList)
        } else {
            tableDao.insertTable(TableBean(id = importId, tableName = "未命名"))
            courseDao.insertCourses(baseList, detailList)
        }
        return baseList.size
    }

}