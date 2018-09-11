package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.SizeUtils

class ScheduleViewModel : ViewModel() {

    var itemHeight = 0
    var marTop = 0
    var showWhite = true
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
    var alpha = 0
    val timeList = arrayListOf<TimeDetailBean>()
    val summerTimeList = arrayListOf<TimeDetailBean>()
    val allCourseList = Array(7) { MutableLiveData<List<CourseBean>>() }
    val loverCourseList = Array(7) { MutableLiveData<List<CourseBean>>() }

    private lateinit var repository: ScheduleRepository

    fun refreshViewData(context: Context) {
        val itemHeightDp = PreferenceUtils.getIntFromSP(context.applicationContext, "item_height", 56)
        itemHeight = SizeUtils.dp2px(context.applicationContext, itemHeightDp.toFloat())
        marTop = context.resources.getDimensionPixelSize(R.dimen.weekItemMarTop)
        textSize = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_text_size", 12)
        showSummerTime = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_summer", false)
        showNone = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show", false)
        showStroke = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_stroke", true)
        showWhite = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_color", true)
        showTimeDetail = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_time_detail", false)
        showSat = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_sat", true)
        showSunday = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_show_weekend", true)
        sundayFirst = PreferenceUtils.getBooleanFromSP(context.applicationContext, "s_sunday_first", false)
        nodesNum = PreferenceUtils.getIntFromSP(context.applicationContext, "classNum", 11)
        alpha = PreferenceUtils.getIntFromSP(context.applicationContext, "sb_alpha", 60)
    }

    fun initRepository(context: Context) {
        repository = ScheduleRepository(context)
    }

    fun getClipboardImportInfo(): LiveData<String> {
        return repository.getClipboardImportInfo()
    }

    fun tranClipboardStr(str: String) {
        repository.tranClipboardStr(str)
    }

    fun tranClipboardList(isLover: Boolean) {
        repository.tranClipboardCourseList(isLover)
    }

    fun getTimeDetailLiveList(): LiveData<List<TimeDetailBean>> {
        return repository.getTimeDetailLiveList()
    }

    fun getSummerTimeLiveList(): LiveData<List<TimeDetailBean>> {
        return repository.getSummerTimeLiveList()
    }

    fun updateFromOldVer(context: Context) {
        repository.updateFromOldVer(context)
    }

    fun getScheduleWidgetIds(): LiveData<List<Int>> {
        return repository.getScheduleWidgetIds()
    }

    fun getCourseByDay(raw: List<CourseBean>): LiveData<List<CourseBean>> {
        return repository.getCourseByDay(raw)
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