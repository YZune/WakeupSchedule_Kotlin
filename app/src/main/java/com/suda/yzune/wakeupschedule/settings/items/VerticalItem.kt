package com.suda.yzune.wakeupschedule.settings.items

data class VerticalItem(
        val name: String,
        val description: String = "",
        val isSpanned: Boolean = false,
        val keys: List<String>? = null) : BaseSettingItem(name, keys) {
    override fun getType(): Int {
        return SettingType.VERTICAL
    }
}