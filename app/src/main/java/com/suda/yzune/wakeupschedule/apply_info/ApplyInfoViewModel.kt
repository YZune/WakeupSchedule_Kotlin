package com.suda.yzune.wakeupschedule.apply_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.bean.HtmlCountBean
import com.suda.yzune.wakeupschedule.utils.MyRetrofitUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ApplyInfoViewModel : ViewModel() {

    val gson = Gson()
    val filterList = arrayListOf<HtmlCountBean>()
    val countList = arrayListOf<HtmlCountBean>()
    val countInfo = MutableLiveData<String>()

    fun initData() {
        MyRetrofitUtils.instance.getService().getHtmlCount().enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response!!.body() != null) {
                    try {
                        countList.clear()
                        countList.addAll(gson.fromJson<List<HtmlCountBean>>(response.body()!!.string(), object : TypeToken<List<HtmlCountBean>>() {
                        }.type))
                        filterList.clear()
                        filterList.addAll(countList)
                        countInfo.value = "OK"
                    } catch (e: JsonSyntaxException) {
                        countInfo.value = "error"
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                countInfo.value = "error"
            }
        })
    }

    fun search(str: String?) {
        filterList.clear()
        if (str.isNullOrBlank()) {
            filterList.addAll(countList)
        } else {
            filterList.addAll(countList.filter {
                it.school.contains(str)
            })
        }
    }
}