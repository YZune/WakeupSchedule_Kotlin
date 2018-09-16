package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object PreferenceUtils {
    private var sp: SharedPreferences? = null
    private lateinit var editor: SharedPreferences.Editor

    fun init(context: Context) {
        if (sp == null) {
            sp = context.applicationContext.getSharedPreferences("config", MODE_PRIVATE)
            editor = sp!!.edit()
        }
    }

    fun saveStringToSP(context: Context, key: String, str: String) {
        init(context)
        editor.putString(key, str)
        editor.apply()
    }

    fun getStringFromSP(context: Context, key: String, defaultString: String): String? {
        init(context)
        return sp!!.getString(key, defaultString)
    }

    fun saveBooleanToSP(context: Context, key: String, t: Boolean) {
        init(context)
        editor.putBoolean(key, t)
        editor.apply()
    }

    fun getBooleanFromSP(context: Context, key: String, defaultBoolean: Boolean): Boolean {
        init(context)
        return sp!!.getBoolean(key, defaultBoolean)
    }

    fun saveIntToSP(context: Context, key: String, i: Int) {
        init(context)
        editor.putInt(key, i)
        editor.apply()
    }

    fun getIntFromSP(context: Context, key: String): Int {
        init(context)
        return sp!!.getInt(key, 0)
    }

    fun getIntFromSP(context: Context, key: String, defaultInt: Int): Int {
        init(context)
        return sp!!.getInt(key, defaultInt)
    }

    fun remove(context: Context, key: String) {
        init(context)
        editor.remove(key)
        editor.apply()
    }

    fun clean(context: Context) {
        init(context)
        editor.clear()
        editor.apply()
    }
}