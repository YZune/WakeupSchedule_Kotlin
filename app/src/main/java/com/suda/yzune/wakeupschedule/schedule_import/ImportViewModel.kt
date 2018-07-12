package com.suda.yzune.wakeupschedule.schedule_import

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.util.Log
import org.jsoup.Jsoup

class ImportViewModel : ViewModel() {
    private val repository = ImportRepository("http://xk.suda.edu.cn")

    fun getCheckCode(): LiveData<Bitmap> {
        repository.checkCode()
        return repository.checkCode
    }

    fun login(id: String, pwd: String, code: String): LiveData<String> {
        repository.login(xh = id, pwd = pwd, code = code)
        return repository.loginResponse
    }

    fun getPrepare(id: String): LiveData<String> {
        repository.getPrepare(xh = id)
        return repository.prepareResponse
    }

    fun toSchedule(id: String, name: String, year: String, term: String): LiveData<String> {
        repository.toSchedule(xh = id, name = name, year = year, term = term)
        return repository.scheduleResponse
    }

    fun parseYears(html: String): List<String>? {
        val option = "option"

        val doc = Jsoup.parse(html)
        val years = arrayListOf<String>()

        val selects = doc.getElementsByTag("select")
        if (selects == null || selects.size < 2) {
            return null
        }

        val options = selects[0].getElementsByTag(option)

        for (o in options) {
            val year = o.text().trim()
            years.add(year)
        }

        Log.d("解析", years.toString())
        return years
    }

    fun parseName(html: String): String {
        val start = html.indexOf(">姓名：")
        return html.substring(start + 4, html.indexOf("</span>", start))
    }
}