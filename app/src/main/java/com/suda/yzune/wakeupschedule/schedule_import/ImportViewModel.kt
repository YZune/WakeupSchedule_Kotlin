package com.suda.yzune.wakeupschedule.schedule_import

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap

class ImportViewModel : ViewModel() {
    private val repository = ImportRepository("http://xk.suda.edu.cn")

    fun getCheckCode(): LiveData<Bitmap> {
        repository.checkCode()
        return repository.checkCode
    }

    fun login(id: String, pwd: String, code: String): LiveData<String> {
        repository.login(xh = id, pwd = pwd, code = code)
        return repository.login_response
    }

    fun toSchedule(id: String, response: String, year: String, term: String){
        val name = response.substring(response.indexOf("id=\"xhxm\">") + 10, response.indexOf("同学</span>"))
        repository.toSchedule(xh = id, name = name, year = year, term = term)
    }
}