package com.suda.yzune.wakeupschedule.schedule_import.login_school.suda

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SudaLoginService {
    @GET("/CheckCode.aspx")
    fun getCheckCode(): Call<ResponseBody>

    @POST("/default2.aspx")
    @FormUrlEncoded
    @Headers("Host: xk.suda.edu.cn", "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
    fun login(@Field("txtUserName") xh: String,
              @Field("Textbox2") pwd: String,
              @Field("txtSecretCode") code: String,
              @Field("Button1") b: String,
              @Field("__VIEWSTATE") view_state: String,
              @Header("Cookie") cookies: String,
              @Field("Textbox1") textbox1: String,
              @Field("RadioButtonList1", encoded = false) rbl: String,
              @Field("lbLanguage") lang: String,
              @Field("hidPdrs") h: String,
              @Field("hidsc") hs: String
    ): Call<ResponseBody>

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

    @GET("/xskbcx.aspx")
    //@FormUrlEncoded
    fun getPrepare(@Query("xh") xh: String,
                   @Header("Referer") referer: String,
                   @Header("Cookie") cookies: String
    ): Call<ResponseBody>

    @POST("/xskbcx.aspx")
    @FormUrlEncoded
    //@Headers("Host: xk.suda.edu.cn", "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
    @Headers("Connection: keep-alive")
    fun getSchedule(@Query("xh") xh: String,
                    @Query("xm") name: String,
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