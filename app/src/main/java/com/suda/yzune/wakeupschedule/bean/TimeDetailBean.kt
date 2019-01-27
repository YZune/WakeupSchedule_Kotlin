package com.suda.yzune.wakeupschedule.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [(
        ForeignKey(entity = TimeTableBean::class,
                parentColumns = ["id"],
                childColumns = ["timeTable"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ))], primaryKeys = ["node", "timeTable"],
        indices = [Index(value = ["timeTable"], unique = false)])
data class TimeDetailBean(
        val node: Int,
        var startTime: String,
        var endTime: String,
        var timeTable: Int = 1
) : Parcelable