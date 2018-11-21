package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.*
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countStr
import com.suda.yzune.wakeupschedule.utils.CourseUtils.getNodeInt
import com.suda.yzune.wakeupschedule.utils.CourseUtils.isContainName
import org.jsoup.Jsoup
import java.io.File
import java.util.regex.Pattern
import kotlin.concurrent.thread

class ImportViewModel(application: Application) : AndroidViewModel(application) {

    var newId = -1
    var importId = -1

    private val dataBase = AppDatabase.getDatabase(application)
    private val tableDao = dataBase.tableDao()
    private val baseDao = dataBase.courseBaseDao()
    private val detailDao = dataBase.courseDetailDao()
    private val timeTableDao = dataBase.timeTableDao()
    private val timeDetailDao = dataBase.timeDetailDao()

    private val pattern = "第.*节"
    private val other = arrayOf("时间", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日", "早晨", "上午", "下午", "晚上")
    private val pattern1 = Pattern.compile("\\{第\\d{1,2}[-]*\\d*周")
    private val WEEK = arrayOf("", "周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val courseProperty = arrayOf("必修课", "选修课", "必修", "选修", "专基", "专选", "公必", "公选", "义修", "选", "必", "主干", "专限", "公基", "值班", "通选",
            "思政必", "思政选", "自基必", "自基选", "语技必", "语技选", "体育必", "体育选", "专业基础课", "双创必", "双创选", "新生必", "新生选", "学科必修", "学科选修",
            "通识必修", "通识选修", "公共基础", "第二课堂", "学科实践", "专业实践", "专业必修", "辅修", "专业选修", "外语", "方向")
    //todo: 在线更新规则
    val newZFSchoolList = arrayListOf("浙江师范大学行知学院", "硅湖职业技术学院", "西南民族大学", "山东理工大学", "江苏工程职业技术学院",
            "南京工业大学", "德州学院", "南京特殊教育师范学院", "济南工程职业技术学院", "吉林建筑大学", "宁波工程学院", "西南大学", "河北师范大学",
            "贵州财经大学", "江苏建筑职业技术学院", "武汉纺织大学", "浙江师范大学")
    val qzLessNodeSchoolList = arrayListOf("锦州医科大学", "山东科技大学", "中国药科大学", "广西师范学院", "天津中医药大学", "山东大学威海校区",
            "江苏师范大学", "吉首大学", "南京理工大学", "天津医科大学", "重庆交通大学", "沈阳工程学院", "韶关学院", "中南财经政法大学")
    val qzMoreNodeSchoolList = arrayListOf("华东理工大学", "中南大学", "湖南商学院", "威海职业学院", "大连外国语大学",
            "中南林业科技大学", "东北林业大学", "齐鲁工业大学", "四川美术学院", "广东财经大学", "南昌航空大学", "皖西学院")
    private var selectedYear = ""
    private var selectedTerm = ""
    private val baseList = arrayListOf<CourseBaseBean>()
    private val detailList = arrayListOf<CourseDetailBean>()
    private val retryList = arrayListOf<Int>()
    val importInfo = MutableLiveData<String>()
    val fileImportInfo = MutableLiveData<String>()
    private var hasTypeFlag = false

    private val repository = ImportRepository("http://xk.suda.edu.cn")

    fun getLastId(): LiveData<Int> {
        return tableDao.getLastId()
    }

    fun getSelectedYear(): String {
        return selectedYear
    }

    fun getSelectedTerm(): String {
        return selectedTerm
    }

    fun getCheckCode(): LiveData<Bitmap> {
        repository.checkCode()
        return repository.checkCode
    }

    fun login(id: String, pwd: String, code: String): LiveData<String> {
        repository.login(xh = id, pwd = pwd, code = code)
        return repository.loginResponse
    }

    fun getPrepare(id: String): LiveData<String> {
        repository.getPrepare(xh = id)
        return repository.prepareResponse
    }

    fun getSelectedSchedule(): String {
        return repository.prepareResponse.value!!
    }

    fun toSchedule(id: String, name: String, year: String, term: String): LiveData<String> {
        repository.toSchedule(xh = id, name = name, year = year, term = term)
        return repository.scheduleResponse
    }

    fun getPostHtmlResponse(): LiveData<String> {
        return repository.postHtmlResponse
    }

    fun postHtml(school: String, type: String, html: String, qq: String) {
        repository.postHtml(school, type, html, qq)
    }

    fun getSchoolInfo(): Array<String> {
        return repository.schoolInfo
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
            for (td in tds) {
                val courseSource = td.text().trim()
                if (courseSource.length <= 1) {
                    continue
                }
                if (Pattern.matches(pattern, courseSource)) {
                    //node number
                    val nodeStr = courseSource.substring(1, courseSource.length - 1)
                    try {
                        node = Integer.decode(nodeStr)
                    } catch (e: Exception) {
                        node = getNodeInt(nodeStr)
                        e.printStackTrace()
                    }
                    continue
                }

                if (inArray(other, courseSource)) {
                    //other list
                    continue
                }
                courses.addAll(parseImportBean(courseSource, node))
                //parseTextInfo(courseSource, node)
            }
        }
        return courses
    }

    private fun parseImportBean(source: String, node: Int): ArrayList<ImportBean> {
        val courses = ArrayList<ImportBean>()
        val split = source.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var preIndex = -1
        Log.d("空", split.size.toString())
        for (i in 0 until split.size) {
            if (split[i].contains('{') && split[i].contains('}')) {
                if (preIndex != -1) {
                    if (split[preIndex - 1] in courseProperty) {
                        hasTypeFlag = true
                    }
                    val temp = ImportBean(startNode = node, name = if (hasTypeFlag && preIndex >= 2) split[preIndex - 2] else split[preIndex - 1],
                            timeInfo = split[preIndex],
                            room = null, teacher = null)
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
                        room = null, teacher = null)
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

    fun importBean2CourseBean(importList: ArrayList<ImportBean>, source: String) {
        baseList.clear()
        detailList.clear()
        retryList.clear()
        var id = 0
        for (importBean in importList) {
            val flag = isContainName(baseList, importBean.name)
            if (flag == -1) {
                baseList.add(CourseBaseBean(id, importBean.name, "", importId))
                val time = parseTime(importBean.timeInfo, importBean.startNode, source, importBean.name)
                detailList.add(CourseDetailBean(
                        id = id, room = importBean.room,
                        teacher = importBean.teacher, day = time[0],
                        step = time[1], startWeek = time[2], endWeek = time[3],
                        type = time[4], startNode = importBean.startNode,
                        tableId = importId
                ))
                if (time[0] == 0) {
                    retryList.add(importList.size - 1)
                }
                id++
            } else {
                val time = parseTime(importBean.timeInfo, importBean.startNode, source, importBean.name)
                detailList.add(CourseDetailBean(
                        id = flag, room = importBean.room,
                        teacher = importBean.teacher, day = time[0],
                        step = time[1], startWeek = time[2], endWeek = time[3],
                        type = time[4], startNode = importBean.startNode,
                        tableId = importId
                ))
                if (time[0] == 0) {
                    retryList.add(importList.size - 1)
                }
            }
        }

        if (retryList.isNotEmpty()) {
            importInfo.value = "retry"
        } else {
            write2DB()
        }
    }

    fun parseNewZF(html: String) {
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

                    day = Integer.parseInt(td.attr("id")[0].toString())

                    val pList = div.getElementsByTag("p")
                    val weekList = arrayListOf<String>()
                    pList.forEach {
                        when (it.getElementsByAttribute("title").attr("title")) {
                            "教师" -> teacher = it.getElementsByTag("font").last().text().trim()
                            "上课地点" -> room = it.getElementsByTag("font").last().text().trim()
                            "节/周" -> {
                                timeStr = it.getElementsByTag("font").last().text().trim()
                                val leftIndex = timeStr.indexOf("$node-")
                                var rightIndex = -1
                                if (leftIndex != -1) {
                                    rightIndex = timeStr.indexOf('节', leftIndex)
                                }
                                if (leftIndex != -1 && rightIndex != -1) {
                                    val endNode = Integer.parseInt(timeStr.substring(leftIndex + "$node-".length, rightIndex))
                                    step = endNode - node + 1
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
                                startWeek = Integer.decode(weeks[0])
                            }
                            if (weeks.size > 1) {
                                endWeek = Integer.decode(weeks[1])
                            }

                            type = when {
                                it.contains('单') -> 1
                                it.contains('双') -> 2
                                else -> 0
                            }
                        } else {
                            startWeek = Integer.decode(it.substring(0, it.indexOf('周')))
                            endWeek = Integer.decode(it.substring(0, it.indexOf('周')))
                        }

                        val flag = isContainName(baseList, courseName)
                        if (flag == -1) {
                            baseList.add(CourseBaseBean(id, courseName, "", importId))
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

        write2DB()
    }

    fun parseQZ(html: String, type: String) {
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
                    val courseElements = div.getElementsByClass("kbcontent")
                    if (courseElements.text().isBlank()) {
                        continue
                    }
                    val courseHtml = courseElements.html()
                    var startIndex = 0
                    var splitIndex = courseHtml.indexOf("-----")
                    while (splitIndex != -1) {
                        when (type) {
                            in qzMoreNodeSchoolList -> convertQZMore(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                            in qzLessNodeSchoolList -> convertQZLess(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                            "北京林业大学" -> convertBeijingLinYeDaXue(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                            "青岛农业大学" -> convertBeijingLinYeDaXue(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                            "广东外语外贸大学" -> convertGuangWai(day, courseHtml.substring(startIndex, splitIndex))
                            "长春大学" -> convertChangChunDaXue(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                        }
                        startIndex = courseHtml.indexOf("<br>", splitIndex) + 4
                        splitIndex = courseHtml.indexOf("-----", startIndex)
                    }
                    when (type) {
                        in qzMoreNodeSchoolList -> convertQZMore(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                        in qzLessNodeSchoolList -> convertQZLess(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                        "北京林业大学" -> convertBeijingLinYeDaXue(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                        "青岛农业大学" -> convertBeijingLinYeDaXue(day, nodeCount, courseHtml.substring(startIndex, splitIndex))
                        "广东外语外贸大学" -> convertGuangWai(day, courseHtml.substring(startIndex, courseHtml.length))
                        "长春大学" -> convertChangChunDaXue(day, nodeCount, courseHtml.substring(startIndex, courseHtml.length))
                    }
                }
            }
        }
        write2DB()
    }

    private fun convertQZMore(day: Int, nodeCount: Int, infoStr: String) {
        val node = nodeCount * 2 - 1
        val courseHtml = Jsoup.parse(infoStr)
        val courseName = Jsoup.parse(infoStr.substringBefore("<font").trim()).text()
        val teacher = courseHtml.getElementsByAttributeValue("title", "老师").text().trim()
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim()
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
                    startWeek = Integer.decode(weeks[0])
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = Integer.decode(weeks[1].substringBefore('('))
                }
            } else {
                startWeek = Integer.decode(it.substringBefore('('))
                endWeek = Integer.decode(it.substringBefore('('))
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "", importId))
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
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim()
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
                    startWeek = Integer.decode(weeks[0])
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = Integer.decode(weeks[1].substringBefore('('))
                }
            } else {
                startWeek = Integer.decode(it.substringBefore('('))
                endWeek = Integer.decode(it.substringBefore('('))
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "", importId))
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
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim()
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
                    startWeek = Integer.decode(weeks[0])
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = Integer.decode(weeks[1].substringBefore('('))
                }
            } else {
                startWeek = Integer.decode(it.substringBefore('('))
                endWeek = Integer.decode(it.substringBefore('('))
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "", importId))
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
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim()
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
                    startWeek = Integer.decode(weeks[0])
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = Integer.decode(weeks[1].substringBefore('('))
                }
            } else {
                startWeek = Integer.decode(it.substringBefore('('))
                endWeek = Integer.decode(it.substringBefore('('))
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "", importId))
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
        val room = courseHtml.getElementsByAttributeValue("title", "教室").text().trim()
        val weekStr = courseHtml.getElementsByAttributeValue("title", "周次(节次)").text().split(' ')[0]
        val nodeList = courseHtml.getElementsByAttributeValue("title", "周次(节次)").text().split(' ')[1].removeSurrounding("[", "]").split('-')
        val weekList = weekStr.split(',')
        var startWeek = 0
        var endWeek = 0
        var id = 0
        var type = 0
        weekList.forEach {
            if (it.contains('-')) {
                val weeks = it.split('-')
                if (weeks.isNotEmpty()) {
                    startWeek = Integer.decode(weeks[0])
                }
                if (weeks.size > 1) {
                    type = when {
                        weeks[1].contains('单') -> 1
                        weeks[1].contains('双') -> 2
                        else -> 0
                    }
                    endWeek = Integer.decode(weeks[1].substringBefore('('))
                }
            } else {
                startWeek = Integer.decode(it.substringBefore('('))
                endWeek = Integer.decode(it.substringBefore('('))
            }

            val flag = isContainName(baseList, courseName)
            if (flag == -1) {
                id = baseList.size
                baseList.add(CourseBaseBean(id, courseName, "", importId))
                detailList.add(CourseDetailBean(
                        id = id, room = room,
                        teacher = teacher, day = day,
                        step = nodeList.last().substringBefore('节').toInt() - nodeList.first().toInt() + 1,
                        startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = nodeList.first().toInt(),
                        tableId = importId
                ))
            } else {
                detailList.add(CourseDetailBean(
                        id = flag, room = room,
                        teacher = teacher, day = day,
                        step = nodeList.last().substringBefore('节').toInt() - nodeList.first().toInt() + 1, startWeek = startWeek, endWeek = endWeek,
                        type = type, startNode = nodeList.first().toInt(),
                        tableId = importId
                ))
            }
        }
    }

    fun importFromFile(path: String) {
        thread(name = "importFromFileThread") {
            try {
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
                val tableId = tableDao.getLastIdInThread() + 1
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
                fileImportInfo.postValue("ok")
            } catch (e: Exception) {
                fileImportInfo.postValue("error")
            }
        }
    }

    private fun write2DB() {
        thread(name = "InitDataThread") {
            //todo: 增量添加课程
            if (newId != importId) {
                baseDao.removeCourseBaseBeanOfTable(importId)
            } else {
                try {
                    tableDao.insertTable(TableBean(id = newId, tableName = "未命名"))
                } catch (e: SQLiteConstraintException) {
                    importInfo.postValue("插入异常")
                }
            }
            try {
                baseDao.insertList(baseList)
                detailDao.insertList(detailList)
                importInfo.postValue("ok")
                //insertResponse.value = "ok"
                Log.d("数据库", "插入")
            } catch (e: SQLiteConstraintException) {
                Log.d("数据库", "插入异常$e")
                importInfo.postValue("插入异常")
                //insertResponse.value = "error"
            }
        }
    }

    private fun parseTime(time: String, startNode: Int, source: String, courseName: String): Array<Int> {
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
                step = Integer.parseInt(time.substring(numLocate - 1, numLocate))
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

            //周数
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
                startWeek = Integer.decode(weeks[0])
                result[2] = startWeek
            }
            if (weeks.size > 1) {
                endWeek = Integer.decode(weeks[1])
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
}