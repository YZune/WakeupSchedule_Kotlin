package tiiehenry.classschedule.json

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ClassInfo(
    val 名称:String,
    val 编号:String,
    val 学分:Double,
    val 类别:String,
    val 必修:Boolean,
    val 上课班号:String,
    val 上课班级名称:String,
    val 任课教师:MutableList<String>,
    val 上课安排:MutableList<ClassTime>

) {
    @Deprecated("")
    fun toJsonObject(): JsonObject {
        return JsonObject().apply {
            addProperty("名称",名称)
            addProperty("编号",编号)
            addProperty("学分",学分)
            addProperty("类别",类别)
            addProperty("必修",必修)
            addProperty("上课班号",上课班号)
            addProperty("上课班级名称",上课班级名称)
            add("任课教师", JsonArray().apply {
                任课教师.forEach {
                    this.add(it)
                }
            })
            add("上课安排", JsonArray().apply {
                上课安排.forEach {
                    this.add(it.toJsonObject())
                }
            })
        }
    }
    override fun toString(): String {
        return Gson().toJson(this)
    }
}