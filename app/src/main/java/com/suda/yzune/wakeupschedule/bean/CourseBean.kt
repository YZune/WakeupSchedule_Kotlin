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
) : Parcelable