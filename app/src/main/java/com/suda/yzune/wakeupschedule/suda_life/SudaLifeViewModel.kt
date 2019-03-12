package com.suda.yzune.wakeupschedule.suda_life

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.bean.SudaResult
import com.suda.yzune.wakeupschedule.bean.SudaRoomData
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*

class SudaLifeViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var buildingData: Map<String, List<String>>
    var roomData = mutableListOf<SudaRoomData>()

    private val roomRetrofit = Retrofit.Builder().baseUrl("http://weixin.suda.edu.cn").build()
    private val roomService = roomRetrofit.create(RoomService::class.java)
    private val gson = Gson()

    suspend fun getDateList(): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val df = SimpleDateFormat("M月dd日", Locale.CHINA)
        list.add(df.format(cal.time))
        repeat(6) {
            cal.add(Calendar.DATE, 1)
            list.add(df.format(cal.time))
        }
        return list
    }

    suspend fun getBuildingData(): String {
        val response = roomService.getBuildingInfo().execute()
        if (response.isSuccessful) {
            val result = response.body()?.string()
            if (result != null) {
                val info = gson.fromJson<SudaResult<Map<String, List<String>>>>(result,
                        object : TypeToken<SudaResult<Map<String, List<String>>>>() {}.type)
                buildingData = info.data
                return "ok"
            } else {
                throw Exception("error")
            }
        } else {
            throw Exception("error")
        }
    }

    suspend fun getRoomData(name: String, date: String): String {
        val response = roomService.getRoomInfo(name, date).execute()
        if (response.isSuccessful) {
            val result = response.body()?.string()
            if (result != null) {
                val info = gson.fromJson<SudaResult<List<SudaRoomData>>>(result,
                        object : TypeToken<SudaResult<List<SudaRoomData>>>() {}.type)
                roomData.clear()
                roomData.addAll(info.data)
                return "ok"
            } else {
                throw Exception("error")
            }
        } else {
            throw Exception("error")
        }
    }

}