package tiiehenry.classschedule.json

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.StringReader

class ClassSchedule(
    val 学校: String,
    val 专业: String,
    val 学期: String,
    val 课程: MutableList<ClassInfo>

) {


    @Deprecated("")
    fun toJsonObject(): JsonObject {
        return JsonObject().apply {
            addProperty("学校", 学校)
            addProperty("专业", 专业)
            addProperty("学期", 学期)
            add("课程", JsonArray().apply {
                课程.forEach {
                    this.add(it.toJsonObject())
                }
            })
        }
    }

    /*  override fun toString(): String {
          return toJsonObject().toString()
      }*/
    override fun toString(): String {
        return Gson().toJson(this)
    }

    companion object {

        @Throws(JsonSyntaxException::class)
        fun fromJsonText(text: String): ClassSchedule {
            val gson = Gson()
            return gson.fromJson(text, ClassSchedule::class.java)
        }
    }


}