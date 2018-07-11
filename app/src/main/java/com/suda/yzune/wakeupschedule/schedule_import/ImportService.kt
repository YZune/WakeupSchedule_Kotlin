package com.suda.yzune.wakeupschedule.schedule_import

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ImportService {
    @GET("/CheckCode.aspx")
    fun getCheckCode(): Call<ResponseBody>

    @POST("/")
    @FormUrlEncoded
    @Headers("Host: xk.suda.edu.cn", "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
    fun login(@Field("TextBox1") xh: String,
              @Field("TextBox2") pwd: String,
              @Field("TextBox3") code: String,
              @Field("Button1") b: String,
              @Field("__VIEWSTATE") view_state: String,
              @Header("Cookie") cookies: String
    ): Call<ResponseBody>

    @POST("/xskbcx.aspx")
    //@FormUrlEncoded
    fun getPrepare(@Query("xh") xh: String,
                   @Header("Referer") referer: String,
                   @Header("Cookie") cookies: String
    ): Call<ResponseBody>

    @POST("/xskbcx.aspx")
    @FormUrlEncoded
    @Headers("Host: xk.suda.edu.cn", "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
    fun getSchedule(@Query("xh") xh: String,
                    @Query("xm", encoded = false) name: String,
                    @Query("gnmkdm") gnmkdm: String,
                    @Field("__EVENTTARGET") event_target: String,
                    @Field("__EVENTARGUMENT") event_argument: String,
                    @Field("__VIEWSTATE") view_state: String,
                    @Header("Cookie") cookies: String,
                    @Header("Referer") referer: String,
                    @Field("xnd") xnd: String,
                    @Field("xqd") xqd: String
    ): Call<ResponseBody>
}