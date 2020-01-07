package com.suda.yzune.wakeupschedule.schedule_import

object Common {

    val otherHeader = arrayOf("时间", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日", "早晨", "上午", "下午", "晚上")
    val courseProperty = arrayOf("任选", "限选", "实践选修", "必修课", "选修课", "必修", "选修", "专基", "专选", "公必", "公选", "义修", "选", "必", "主干", "专限", "公基", "值班", "通选",
            "思政必", "思政选", "自基必", "自基选", "语技必", "语技选", "体育必", "体育选", "专业基础课", "双创必", "双创选", "新生必", "新生选", "学科必修", "学科选修",
            "通识必修", "通识选修", "公共基础", "第二课堂", "学科实践", "专业实践", "专业必修", "辅修", "专业选修", "外语", "方向", "专业必修课", "全选")

    private val headerNodePattern = Regex(""""第.*节""")

    fun parseHeaderNodeString(str: String): Int {
        var node = -1
        if (headerNodePattern.matches(str)) {
            val nodeStr = str.substring(1, str.length - 1)
            node = try {
                nodeStr.toInt()
            } catch (e: Exception) {
                getNodeInt(nodeStr)
            }
        }
        return node
    }

    private fun getNodeInt(nodeStr: String): Int {
        return when (nodeStr) {
            "一" -> 1
            "二" -> 2
            "三" -> 3
            "四" -> 4
            "五" -> 5
            "六" -> 6
            "七" -> 7
            "日" -> 7
            "八" -> 8
            "九" -> 9
            "十" -> 10
            "十一" -> 11
            "十二" -> 12
            "十三" -> 13
            "十四" -> 14
            "十五" -> 15
            "十六" -> 16
            "十七" -> 17
            "十八" -> 18
            "十九" -> 19
            "二十" -> 20
            else -> -1
        }
    }

}