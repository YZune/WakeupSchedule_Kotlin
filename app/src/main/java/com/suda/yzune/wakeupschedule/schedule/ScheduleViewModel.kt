package com.suda.yzune.wakeupschedule.schedule

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.CourseBean
import com.suda.yzune.wakeupschedule.bean.TimeDetailBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils

class ScheduleViewModel : ViewModel() {

    private lateinit var repository: ScheduleRepository

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

    fun getTimeList(): ArrayList<TimeDetailBean> {
        return repository.getTimeDetailList()
    }

    fun getSummerTimeList(): ArrayList<TimeDetailBean> {
        return repository.getSummerTimeList()
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