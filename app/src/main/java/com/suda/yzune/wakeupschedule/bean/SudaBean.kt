package com.suda.yzune.wakeupschedule.bean

data class SudaResult<T>(
        val `data`: T,
        val errorcode: Int,
        val msg: String,
        val success: Boolean
)

data class SudaRoomData(
        val jsbh: String,
        val kfj: String
)