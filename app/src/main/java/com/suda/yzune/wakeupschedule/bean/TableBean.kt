package com.suda.yzune.wakeupschedule.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [(
        ForeignKey(entity = TimeTableBean::class,
                parentColumns = ["id"],
                childColumns = ["timeTable"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.SET_DEFAULT
        ))],
        indices = [Index(value = ["timeTable"], unique = false)])
data class TableBean(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var tableName: String,
        var nodes: Int = 20,
        var background: String = "",
        var timeTable: Int = 1,
        var startDate: String = "2020-02-03",
        var maxWeek: Int = 30,
        var itemHeight: Int = 56,
        var itemAlpha: Int = 60,
        var itemTextSize: Int = 12,
        var widgetItemHeight: Int = 56,
        var widgetItemAlpha: Int = 60,
        var widgetItemTextSize: Int = 12,
        var strokeColor: Int = 0x80ffffff.toInt(),
        var widgetStrokeColor: Int = 0x80ffffff.toInt(),
        var textColor: Int = 0xff000000.toInt(),
        var widgetTextColor: Int = 0xff000000.toInt(),
        var courseTextColor: Int = 0xffffffff.toInt(),
        var widgetCourseTextColor: Int = 0xffffffff.toInt(),
        var showSat: Boolean = true,
        var showSun: Boolean = true,
        var sundayFirst: Boolean = false,
        var showOtherWeekCourse: Boolean = true,
        var showTime: Boolean = false,
        var type: Int = 0
) : Parcelable