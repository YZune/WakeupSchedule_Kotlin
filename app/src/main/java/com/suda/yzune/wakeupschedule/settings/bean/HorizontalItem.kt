package com.suda.yzune.wakeupschedule.settings.bean

data class HorizontalItem(
        val title: String,
        var value: String,
        val keys: List<String>? = null) : BaseItem(keys)