package tiiehenry.classschedule.json

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class ClassTime(
    val 周次:MutableList<String>,
    val 星期:MutableList<Int>,
    val 节次:MutableList<String>,
    val 地点:String,
    val 教师:String
) {
    @Deprecated("")
    fun toJsonObject(): JsonObject {
        return JsonObject().apply {
            add("周次", JsonArray().apply {
                周次.forEach {
                    this.add(it)
                }
            })
            add("星期", JsonArray().apply {
                周次.forEach {
                    this.add(it)
                }
            })
            add("节次", JsonArray().apply {
                周次.forEach {
                    this.add(it)
                }
            })
            addProperty("地点",地点)
            addProperty("教师",教师)
        }
    }
    override fun toString(): String {
        return Gson().toJson(this)
    }
}