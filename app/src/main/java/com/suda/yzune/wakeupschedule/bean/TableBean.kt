package com.suda.yzune.wakeupschedule.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

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
        var nodes: Int = 11,
        var background: String = "",
        var timeTable: Int = 0,
        var startDate: String = "2018-09-03",
        var maxWeek: Int = 30,
        var itemHeight: Int = 56,
        var itemAlpha: Int = 60,
        var itemTextSize: Int = 12,
        var widgetItemHeight: Int = 56,
        var widgetItemAlpha: Int = 60,
        var widgetItemTextSize: Int = 12,
        var strokeColor: String = "#80ffffff",
        var textColor: String = "#000000",
        var widgetTextColor: String = "#000000",
        var showSat: Boolean = true,
        var showSun: Boolean = true,
        var sundayFirst: Boolean = false,
        var showOtherWeekCourse: Boolean = false,
        var showTime: Boolean = false,
        var type: Int = 0
)