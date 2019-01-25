package com.suda.yzune.wakeupschedule.settings.bean

data class VerticalItem(
        val title: String,
        val description: String,
        val isSpanned: Boolean = false,
        val keys: List<String>? = null) : BaseItem(keys)