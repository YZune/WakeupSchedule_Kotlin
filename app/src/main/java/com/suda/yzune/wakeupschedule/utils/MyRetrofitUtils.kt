package com.suda.yzune.wakeupschedule.utils

import android.app.Activity
import android.content.Context
import es.dmoral.toasty.Toasty
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MyRetrofitUtils private constructor() {
    private val retrofit = Retrofit.Builder().baseUrl("http://106.15.202.52:8080").build()
    private val myService = retrofit.create(MyRetrofitService::class.java)

    fun getService(): MyRetrofitService {
        return myService
    }

    fun addCount(context: Context) {
        myService.addCount().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                PreferenceUtils.saveBooleanToSP(context, "has_count", true)
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}
        })
    }

    fun postHtml(school: String, type: String, html: String, qq: String, activity: Activity) {
        myService.postHtml(school, type, html, qq).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toasty.error(activity.applicationContext, "上传网页源码失败，请检查网络").show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.body()!!.string() == "OK") {
                    Toasty.success(activity.applicationContext, "申请成功~").show()
                    activity.finish()
                } else {
                    Toasty.error(activity.applicationContext, "上传网页源码失败，请检查网络").show()
                }
            }
        })
    }

    companion object {
        val instance: MyRetrofitUtils by lazy { MyRetrofitUtils() }
    }
}