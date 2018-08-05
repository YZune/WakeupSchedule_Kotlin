package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

object PreferenceUtils {
    fun saveStringToSP(context: Context, key: String, str: String) {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(key, str)
        editor.apply()
    }

    fun getStringFromSP(context: Context, key: String, defaultString: String): String? {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        return sp.getString(key, defaultString)
    }

    fun saveBooleanToSP(context: Context, key: String, t: Boolean) {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean(key, t)
        editor.apply()
    }

    fun getBooleanFromSP(context: Context, key: String): Boolean {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        return sp.getBoolean(key, true)
    }

    fun getBooleanFromSP(context: Context, key: String, defaultBoolean: Boolean): Boolean {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        return sp.getBoolean(key, defaultBoolean)
    }

    fun saveIntToSP(context: Context, key: String, i: Int) {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(key, i)
        editor.apply()
    }

    fun getIntFromSP(context: Context, key: String): Int {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        return sp.getInt(key, 0)
    }

    fun getIntFromSP(context: Context, key: String, defaultInt: Int): Int {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        return sp.getInt(key, defaultInt)
    }

    fun clean(context: Context) {
        val sp = context.getSharedPreferences("config", MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.apply()
    }
}