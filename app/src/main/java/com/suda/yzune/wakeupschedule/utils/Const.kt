package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

fun Context.getPrefer(name: String = "config"): SharedPreferences = getSharedPreferences(name, MODE_PRIVATE)

object Const {

    const val REQUEST_CODE_EXPORT = 100
    const val REQUEST_CODE_IMPORT = 101
    const val REQUEST_CODE_SCHEDULE_SETTING = 102
    const val REQUEST_CODE_EXPORT_ICS = 103
    const val REQUEST_CODE_IMPORT_FILE = 104
    const val REQUEST_CODE_IMPORT_HTML = 105
    const val REQUEST_CODE_IMPORT_CSV = 106
    const val REQUEST_CODE_CHOOSE_SCHOOL = 107
    const val REQUEST_CODE_ADD_COURSE = 108

    const val KEY_OLD_VERSION_COURSE = "course"
    const val KEY_OLD_VERSION_BG_URI = "pic_uri"
    const val KEY_OLD_VERSION_TERM_START = "termStart"
    const val KEY_HAS_ADJUST = "has_adjust"

    const val KEY_IMPORT_SCHOOL = "import_school"
    const val KEY_SCHOOL_URL = "school_url"
    const val KEY_DAY_NIGHT_THEME = "day_night_theme"
    const val KEY_HIDE_NAV_BAR = "hide_main_nav_bar"
    const val KEY_COURSE_REMIND = "course_reminder"
    const val KEY_REMINDER_ON_GOING = "reminder_on_going"
    const val KEY_SILENCE_REMINDER = "silence_reminder"
    const val KEY_HAS_COUNT = "has_count"
    const val KEY_CHECK_UPDATE = "s_update"
    const val KEY_DAY_WIDGET_COLOR = "s_colorful_day_widget"
    const val KEY_SHOW_EMPTY_VIEW = "show_empty_view"
    const val KEY_SHOW_SUDA_LIFE = "suda_life"
    const val KEY_THEME_COLOR = "nav_bar_color"
    const val KEY_REMINDER_TIME = "reminder_min"
    const val KEY_OPEN_TIMES = "open_times"
    const val KEY_HAS_INTRO = "has_intro"
    const val KEY_SCHEDULE_PRE_LOAD = "schedule_pre_load"
    const val KEY_SCHEDULE_BLANK_AREA = "schedule_blank_area"
    const val KEY_SCHEDULE_DETAIL_TIME = "schedule_detail_time"

}