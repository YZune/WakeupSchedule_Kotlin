package com.suda.yzune.wakeupschedule

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.dao.CourseBaseDao
import android.arch.persistence.room.Room
import android.content.Context
import com.suda.yzune.wakeupschedule.dao.CourseDetailDao


@Database(entities = [CourseBaseBean::class, CourseDetailBean::class],
        version = 5, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, "wakeup")
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    abstract fun courseBaseDao(): CourseBaseDao

    abstract fun courseDetailDao(): CourseDetailDao

}