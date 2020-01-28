package com.suda.yzune.wakeupschedule.suda_life

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suda.yzune.wakeupschedule.bean.BathData
import com.suda.yzune.wakeupschedule.bean.BathResponse
import com.suda.yzune.wakeupschedule.bean.SudaResult
import com.suda.yzune.wakeupschedule.bean.SudaRoomData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*

class SudaLifeViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var buildingData: Map<String, List<String>>
    var roomData = mutableListOf<SudaRoomData>()
    lateinit var maleBathData: BathData
    lateinit var femaleBathData: BathData

    private val roomRetrofit = Retrofit.Builder().baseUrl("http://weixin.suda.edu.cn").build()
    private val roomService = roomRetrofit.create(RoomService::class.java)
    private val bathRetrofit = Retrofit.Builder().baseUrl("http://mapp.suda.edu.cn").build()
    private val bathService = bathRetrofit.create(BathService::class.java)
    private val gson = Gson()

    fun getDateList(): List<String> {
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

    suspend fun getBathData(male: Boolean): String {
        val response = withContext(Dispatchers.IO) {
            if (male) {
                bathService.getBathInfo("7FC7FBA6EBCC4E5EBCEBB0B45A6EAE51").execute()
            } else {
                bathService.getBathInfo("75DED595960A4F2B97E65CAB06325766").execute()
            }
        }
        if (response.isSuccessful) {
            val result = withContext(Dispatchers.IO) { response.body()?.string() }
            if (result != null) {
                val info = gson.fromJson<BathResponse>(result,
                        object : TypeToken<BathResponse>() {}.type)
                if (info.result.data.isNotEmpty()) {
                    if (male) {
                        maleBathData = info.result.data[0]
                    } else {
                        femaleBathData = info.result.data[0]
                    }
                    return "ok"
                } else {
                    throw Exception("error")
                }
            } else {
                throw Exception("error")
            }
        } else {
            throw Exception("error")
        }
    }

    suspend fun getBuildingData(): String {
        val response = withContext(Dispatchers.IO) { roomService.getBuildingInfo().execute() }
        if (response.isSuccessful) {
            val result = withContext(Dispatchers.IO) { response.body()?.string() }
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
        val response = withContext(Dispatchers.IO) { roomService.getRoomInfo(name, date).execute() }
        if (response.isSuccessful) {
            val result = withContext(Dispatchers.IO) { response.body()?.string() }
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