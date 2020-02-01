package com.suda.yzune.wakeupschedule.settings.items

data class CategoryItem(val name: String, val hasMarginTop: Boolean) : BaseSettingItem(null) {
    override fun getType() = SettingType.CATEGORY
}