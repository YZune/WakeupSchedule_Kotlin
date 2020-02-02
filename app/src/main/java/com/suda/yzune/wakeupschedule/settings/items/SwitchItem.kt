package com.suda.yzune.wakeupschedule.settings.items

data class SwitchItem(
        val title: String,
        var checked: Boolean,
        var desc: String = "",
        val keys: List<String>? = null) : BaseSettingItem(keys) {
    override fun getType(): Int {
        return SettingType.SWITCH
    }
}