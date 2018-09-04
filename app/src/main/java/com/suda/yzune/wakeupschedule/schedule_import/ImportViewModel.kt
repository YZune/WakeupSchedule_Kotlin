package com.suda.yzune.wakeupschedule.schedule_import

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.util.Log
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.ImportBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils.countStr
import com.suda.yzune.wakeupschedule.utils.CourseUtils.getNodeInt
import com.suda.yzune.wakeupschedule.utils.CourseUtils.isContainName
import org.jsoup.Jsoup
import java.util.regex.Pattern
import kotlin.concurrent.thread

class ImportViewModel : ViewModel() {

    private val pattern = "第.*节"
    private val other = arrayOf("时间", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日", "早晨", "上午", "下午", "晚上")
    private val pattern1 = Pattern.compile("\\{第\\d{1,2}[-]*\\d*周")
    private val WEEK = arrayOf("", "周一", "周二", "周三", "周四", "周五", "周六", "周日")
    private val courseProperty = arrayOf("必修", "选修", "专基", "专选", "公必", "公选", "义修", "选", "必", "主干", "专限", "公基", "值班", "通选",
            "思政必", "思政选", "自基必", "自基选", "语技必", "语技选", "体育必", "体育选", "专业基础课", "双创必", "双创选", "新生必", "新生选", "学科必修", "学科选修",
            "通识必修", "通识选修", "公共基础", "第二课堂", "学科实践")
    //todo: 在线更新规则
    private var selectedYear = ""
    private var selectedTerm = ""
    private val baseList = arrayListOf<CourseBaseBean>()
    private val detailList = arrayListOf<CourseDetailBean>()
    private val retryList = arrayListOf<Int>()
    private val importInfo = MutableLiveData<String>()

    private val repository = ImportRepository("http://xk.suda.edu.cn")

    fun getImportInfo(): LiveData<String> {
        return importInfo
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
                        node = Integer.decode(nodeStr)!!
                    } catch (e: Exception) {
                        node = getNodeInt(nodeStr)
                        e.printStackTrace()
                    }
                    continue
                }

                if (inArray(other, courseSource)) {
                    //other data
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
                    val temp = ImportBean(startNode = node, name = if (split[preIndex - 1] in courseProperty) split[preIndex - 2] else split[preIndex - 1],
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
                val temp = ImportBean(startNode = node, name = if (split[preIndex - 1] in courseProperty) split[preIndex - 2] else split[preIndex - 1],
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

    fun importBean2CourseBean(importList: java.util.ArrayList<ImportBean>, tableName: String, context: Context, sourse: String) {
        baseList.clear()
        detailList.clear()
        retryList.clear()
        var id = 0
        for (importBean in importList) {
            val flag = isContainName(baseList, importBean.name)
            if (flag == -1) {
                baseList.add(CourseBaseBean(id, importBean.name, "", tableName))
                val time = parseTime(importBean.timeInfo, importBean.startNode, sourse, importBean.name)
                detailList.add(CourseDetailBean(
                        id = id, room = importBean.room,
                        teacher = importBean.teacher, day = time[0],
                        step = time[1], startWeek = time[2], endWeek = time[3],
                        type = time[4], startNode = importBean.startNode,
                        tableName = tableName
                ))
                if (time[0] == 0) {
                    retryList.add(importList.size - 1)
                }
                id++
            } else {
                val time = parseTime(importBean.timeInfo, importBean.startNode, sourse, importBean.name)
                detailList.add(CourseDetailBean(
                        id = flag, room = importBean.room,
                        teacher = importBean.teacher, day = time[0],
                        step = time[1], startWeek = time[2], endWeek = time[3],
                        type = time[4], startNode = importBean.startNode,
                        tableName = tableName
                ))
                if (time[0] == 0) {
                    retryList.add(importList.size - 1)
                }
            }
        }

        if (retryList.isNotEmpty()) {
            importInfo.value = "retry"
        } else {
            write2DB(context)
        }
    }

    fun parseNewFZ(html: String, tableName: String, context: Context) {
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
                val courseValue = td.text().trim()

                if (courseValue.length <= 1) {
                    continue
                }

                var courseName = td.getElementsByClass("title").text()
                if (courseName.isEmpty()) {
                    continue
                }
                if (courseName.contains('★') || courseName.contains('☆')) {
                    courseName = courseName.substring(0, courseName.length - 1)
                }

                day = Integer.parseInt(td.attr("id")[0].toString())

                val pList = td.getElementsByTag("p")
                val weekList = arrayListOf<String>()
                pList.forEach {
                    when (it.getElementsByAttribute("title").attr("title")) {
                        "教师" -> teacher = it.getElementsByTag("font").last().text().trim()
                        "上课地点" -> room = it.getElementsByTag("font").last().text().trim()
                        "节/周" -> {
                            timeStr = it.getElementsByTag("font").last().text().trim()
                            val leftIndex = timeStr.indexOf('-')
                            val rightIndex = timeStr.indexOf('节')
                            if (leftIndex != -1 && rightIndex != -1) {
                                val endNode = Integer.parseInt(timeStr.substring(leftIndex + 1, rightIndex))
                                step = endNode - node + 1
                            }
                            weekList.clear()
                            weekList.addAll(timeStr.substring(rightIndex + 2).split(','))
                        }
                    }
                }

                weekList.forEach {
                    val weeks = it.substring(0, it.indexOf('周')).split('-')
                    if (weeks.isNotEmpty()) {
                        startWeek = Integer.decode(weeks[0])!!
                    }
                    if (weeks.size > 1) {
                        endWeek = Integer.decode(weeks[1])!!
                    }

                    type = when {
                        it.contains('单') -> 1
                        it.contains('双') -> 2
                        else -> 0
                    }

                    val flag = isContainName(baseList, courseName)
                    if (flag == -1) {
                        baseList.add(CourseBaseBean(id, courseName, "", tableName))
                        detailList.add(CourseDetailBean(
                                id = id, room = room,
                                teacher = teacher, day = day,
                                step = step, startWeek = startWeek, endWeek = endWeek,
                                type = type, startNode = node,
                                tableName = tableName
                        ))
                        id++
                    } else {
                        if (detailList[flag].room == room &&
                                detailList[flag].startWeek == startWeek &&
                                (detailList[flag].startNode + detailList[flag].step == node)) {
                            detailList[flag].step += step
                        } else {
                            detailList.add(CourseDetailBean(
                                    id = flag, room = room,
                                    teacher = teacher, day = day,
                                    step = step, startWeek = startWeek, endWeek = endWeek,
                                    type = type, startNode = node,
                                    tableName = tableName
                            ))
                        }
                    }
                }
            }
        }

        write2DB(context)
    }

    private fun write2DB(context: Context) {
        val dataBase = AppDatabase.getDatabase(context)
        val baseDao = dataBase.courseBaseDao()
        val detailDao = dataBase.courseDetailDao()

        thread(name = "InitDataThread") {
            baseDao.removeCourseData("")
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
            val startIndex = source.indexOf("<td>第${startNode}节</td>")
            val endIndex = source.indexOf(courseName, startIndex)
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
                startWeek = Integer.decode(weeks[0])!!
                result[2] = startWeek
            }
            if (weeks.size > 1) {
                endWeek = Integer.decode(weeks[1])!!
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