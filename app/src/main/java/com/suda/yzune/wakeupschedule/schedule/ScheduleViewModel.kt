package com.suda.yzune.wakeupschedule.schedule

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    var itemHeight = 0
    var marTop = 0
    var showWhite = false
    var showSunday = true
    var sundayFirst = false
    var showSat = true
    var showTimeDetail = false
    var showSummerTime = false
    var showStroke = true
    var showNone = true
    var nodesNum = 11
    var textSize = 12
    var savedWeek = -1
    var maxWeek = 0
    var alpha = 0
    val timeList = arrayListOf<TimeDetailBean>()
    val summerTimeList = arrayListOf<TimeDetailBean>()
    val allCourseList = Array(7) { MutableLiveData<List<CourseBean>>() }
    val loverCourseList = Array(7) { MutableLiveData<List<CourseBean>>() }
    private val json = PreferenceUtils.getStringFromSP(getApplication(), "course", "")!!
    val clipboardImportInfo = MutableLiveData<String>()
    var clipboardCourseList: List<CourseBean>? = null
    private val repository = ScheduleRepository(getApplication())

    fun refreshViewData(context: Context) {
        val itemHeightDp = PreferenceUtils.getIntFromSP(getApplication(), "item_height", 56)
        itemHeight = SizeUtils.dp2px(getApplication(), itemHeightDp.toFloat())
        marTop = context.resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
        maxWeek = PreferenceUtils.getIntFromSP(getApplication(), "sb_weeks", 30)
        textSize = PreferenceUtils.getIntFromSP(getApplication(), "sb_text_size", 12)
        showSummerTime = PreferenceUtils.getBooleanFromSP(getApplication(), "s_summer", false)
        showNone = PreferenceUtils.getBooleanFromSP(getApplication(), "s_show", false)
        showStroke = PreferenceUtils.getBooleanFromSP(getApplication(), "s_stroke", true)
        showWhite = PreferenceUtils.getBooleanFromSP(getApplication(), "s_color", false)
        showTimeDetail = PreferenceUtils.getBooleanFromSP(getApplication(), "s_show_time_detail", false)
        showSat = PreferenceUtils.getBooleanFromSP(getApplication(), "s_show_sat", true)
        showSunday = PreferenceUtils.getBooleanFromSP(getApplication(), "s_show_weekend", true)
        sundayFirst = PreferenceUtils.getBooleanFromSP(getApplication(), "s_sunday_first", false)
        nodesNum = PreferenceUtils.getIntFromSP(getApplication(), "classNum", 11)
        alpha = PreferenceUtils.getIntFromSP(getApplication(), "sb_alpha", 60)
    }

    fun tranClipboardList(tableName: String) {
        if (clipboardCourseList == null) return
        val baseList = arrayListOf<CourseBaseBean>()
        val detailList = arrayListOf<CourseDetailBean>()
        clipboardCourseList!!.forEach {
            baseList.add(CourseUtils.courseBean2BaseBean(it).apply {
                this.tableName = tableName
            })

            detailList.add(CourseUtils.courseBean2DetailBean(it).apply {
                this.tableName = tableName
            })
        }

        repository.write2DB(baseList, detailList, tableName, clipboardImportInfo)
    }

    fun tranClipboardStr(str: String) {
        val gson = Gson()
        try {
            clipboardCourseList = gson.fromJson<List<CourseBean>>(str.substring(15), object : TypeToken<List<CourseBean>>() {}.type)
        } catch (e: JsonSyntaxException) {
            clipboardImportInfo.value = "解析异常"
            clipboardCourseList = null
        }
    }

    fun getTimeDetailLiveList(): LiveData<List<TimeDetailBean>> {
        return repository.getTimeDetailLiveList()
    }

    fun getSummerTimeLiveList(): LiveData<List<TimeDetailBean>> {
        return repository.getSummerTimeLiveList()
    }

    fun updateFromOldVer(context: Context) {
        repository.updateFromOldVer(context, json)
    }

    fun getScheduleWidgetIds(): LiveData<List<Int>> {
        return repository.getScheduleWidgetIds()
    }

    fun getRawCourseByDay(day: Int, tableName: String = ""): LiveData<List<CourseBean>> {
        return repository.getRawCourseByDay(day, tableName)
    }

    fun getCourse(): LiveData<List<CourseBean>> {
        return repository.getCourse()
    }

    fun deleteCourseBean(courseBean: CourseBean) {
        repository.deleteCourseBean(courseBean)
    }

    fun deleteCourseBaseBean(courseBean: CourseBean) {
        repository.deleteCourseBaseBean(courseBean.id, courseBean.tableName)
    }

    fun updateCourseBaseBean(courseBean: CourseBean) {
        repository.updateCourseBaseBean(CourseUtils.courseBean2BaseBean(courseBean))
    }

}