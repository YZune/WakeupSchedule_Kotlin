package com.suda.yzune.wakeupschedule.schedule_import.parser.qz

class QzCrazyParser(source: String) : QzParser(source) {
    override val tableName: String
        get() = "kbcontent1"
}