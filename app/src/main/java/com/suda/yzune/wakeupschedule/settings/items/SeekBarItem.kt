package com.suda.yzune.wakeupschedule.settings.items

data class SeekBarItem(
        val title: String,
        var valueInt: Int,
        val min: Int,
        var max: Int,
        val unit: String,
        val prefix: String = "",
        val keys: List<String>? = null) : BaseSettingItem(keys) {
    override fun getType(): Int {
        return SettingType.SEEKBAR
    }
}