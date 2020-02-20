package com.suda.yzune.wakeupschedule.settings.items

data class CategoryItem(val name: String, val hasMarginTop: Boolean) : BaseSettingItem(name, null) {
    override fun getType() = SettingType.CATEGORY
}