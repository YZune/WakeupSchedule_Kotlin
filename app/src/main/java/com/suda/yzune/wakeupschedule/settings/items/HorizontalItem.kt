package com.suda.yzune.wakeupschedule.settings.items

data class HorizontalItem(
        val title: String,
        var value: String,
        val keys: List<String>? = null) : BaseSettingItem(keys) {
    override fun getType(): Int {
        return SettingType.HORIZON
    }
}