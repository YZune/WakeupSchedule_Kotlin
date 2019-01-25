package com.suda.yzune.wakeupschedule.settings.bean

data class SeekBarItem(
        val title: String,
        var valueInt: Int,
        val min: Int,
        val max: Int,
        val unit: String,
        val keys: List<String>? = null) : BaseItem(keys)