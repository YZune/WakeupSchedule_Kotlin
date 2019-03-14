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

data class BathResponse(
        val errorMsg: Any,
        val result: BathResult,
        val resultCode: Int
)

data class BathResult(
        val `data`: List<BathData>,
        val total: Int
)

data class BathData(
        val inNum: Int,
        val outNum: Int,
        val recordTime: String,
        val shopCode: String,
        val shopId: String,
        val shopName: String
)