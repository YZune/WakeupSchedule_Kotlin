package com.suda.yzune.wakeupschedule.suda_life

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP

interface RoomService {
    @GET("/_web/_customize/suda/freeEmptyRoom/fetchCampusBuilding.do?_p=YXM9MiZ0PTQmcD0xJm09TiY_")
    fun getBuildingInfo(): Call<ResponseBody>

    @HTTP(method = "POST", path = "/_web/_customize/suda/freeEmptyRoom/fetchEmptyRoomsInfo.do?_p=YXM9MiZ0PTQmcD0xJm09TiY_", hasBody = true)
    @FormUrlEncoded
    fun getRoomInfo(@Field("lh") buildingName: String,
                    @Field("rq") date: String
    ): Call<ResponseBody>
}