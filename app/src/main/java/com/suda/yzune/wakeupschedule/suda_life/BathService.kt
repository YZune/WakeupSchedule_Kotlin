package com.suda.yzune.wakeupschedule.suda_life

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BathService {

    @GET("/_web/_lightapp/bathhouse/queryBathhouse.rst")
    fun getBathInfo(@Query("shopCode") shopCode: String): Call<ResponseBody>

}