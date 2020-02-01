package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

fun Context.getPrefer(name: String = "config"): SharedPreferences = getSharedPreferences(name, MODE_PRIVATE)

object PreferenceKeys {

    const val OLD_VERSION_COURSE = "course"
    const val OLD_VERSION_BG_URI = "pic_uri"
    const val OLD_VERSION_TERM_START = "termStart"
    const val HAS_ADJUST = "has_adjust"

    const val IMPORT_SCHOOL = "import_school"
    const val SCHOOL_URL = "school_url"
    const val DAY_NIGHT_THEME = "day_night_theme"
    const val HIDE_NAV_BAR = "hide_main_nav_bar"
    const val COURSE_REMIND = "course_reminder"
    const val REMINDER_ON_GOING = "reminder_on_going"
    const val SILENCE_REMINDER = "silence_reminder"
    const val HAS_COUNT = "has_count"
    const val CHECK_UPDATE = "s_update"
    const val SHOW_DAY_WIDGET_COLOR = "s_colorful_day_widget"
    const val SHOW_SUDA_LIFE = "suda_life"
    const val THEME_COLOR = "nav_bar_color"
    const val REMINDER_TIME = "reminder_min"
    const val OPEN_TIMES = "open_times"
    const val HAS_INTRO = "has_intro"

}