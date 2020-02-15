package com.suda.yzune.wakeupschedule.utils

import android.content.Context
import androidx.core.content.edit
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MyRetrofitUtils private constructor() {
    private val retrofit = Retrofit.Builder().baseUrl("https://i.wakeup.fun/").build()
    private val myService = retrofit.create(MyRetrofitService::class.java)

    fun getService(): MyRetrofitService {
        return myService
    }

    fun addCount(context: Context) {
        myService.addCount().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                context.getPrefer().edit {
                    putBoolean(Const.KEY_HAS_COUNT, true)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}
        })
    }

    companion object {
        val instance: MyRetrofitUtils by lazy { MyRetrofitUtils() }
    }
}