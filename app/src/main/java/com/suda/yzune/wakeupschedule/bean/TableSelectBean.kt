package com.suda.yzune.wakeupschedule.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TableSelectBean(
        var id: Int,
        var tableName: String,
        var background: String = "",
        var maxWeek: Int = 30,
        var nodes: Int = 12,
        var type: Int = 0
) : Parcelable