package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MyRetrofitUtils private constructor() {
    private val retrofit = Retrofit.Builder().baseUrl("https://www.wakeup.fun:8443").build()
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

    companion object {
        val instance: MyRetrofitUtils by lazy { MyRetrofitUtils() }
    }
}