package com.suda.yzune.wakeupschedule.utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface MyRetrofitService {
    @GET("/school/count")
    fun addCount(): Call<ResponseBody>

    @GET("/school/get_donate")
    fun getDonateList(): Call<ResponseBody>

    @GET("/school/getupdate")
    fun getUpdateInfo(): Call<ResponseBody>
}