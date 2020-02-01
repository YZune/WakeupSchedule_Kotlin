package com.suda.yzune.wakeupschedule.settings.items

data class VerticalItem(
        val title: String,
        val description: String = "",
        val isSpanned: Boolean = false,
        val keys: List<String>? = null) : BaseSettingItem(keys) {
    override fun getType(): Int {
        return SettingType.VERTICAL
    }
}