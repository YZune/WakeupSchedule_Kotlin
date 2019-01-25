package com.suda.yzune.wakeupschedule.settings.bean

data class SwitchItem(
        val title: String,
        var checked: Boolean,
        val keys: List<String>? = null) : BaseItem(keys)