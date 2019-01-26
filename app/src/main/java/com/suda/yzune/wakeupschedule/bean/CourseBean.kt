package com.suda.yzune.wakeupschedule.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CourseBean(
        var id: Int,
        var courseName: String,
        var day: Int,
        var room: String?,
        var teacher: String?,
        var startNode: Int,
        var step: Int,
        var startWeek: Int,
        var endWeek: Int,
        var type: Int,
        var color: String,
        var tableId: Int
) : Parcelable {

    fun getNodeString(): String {
        return "第$startNode - ${startNode + step - 1}节"
    }

    fun inWeek(week: Int): Boolean {
        return when (type) {
            0 -> {
                (startWeek <= week) && (week <= endWeek)
            }
            1 -> {
                (startWeek <= week) && (week <= endWeek) && (week % 2 == 1)
            }
            2 -> {
                (startWeek <= week) && (week <= endWeek) && (week % 2 == 0)
            }
            else -> false
        }
    }
}