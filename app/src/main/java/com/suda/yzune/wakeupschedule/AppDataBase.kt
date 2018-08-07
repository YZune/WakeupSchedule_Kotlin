package com.suda.yzune.wakeupschedule

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean
import com.suda.yzune.wakeupschedule.dao.CourseBaseDao
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.suda.yzune.wakeupschedule.bean.AppWidgetBean
import com.suda.yzune.wakeupschedule.dao.AppWidgetDao
import com.suda.yzune.wakeupschedule.dao.CourseDetailDao


@Database(entities = [CourseBaseBean::class, CourseDetailBean::class, AppWidgetBean::class],
        version = 6, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, "wakeup")
                                .addMigrations(migrate)
                                .allowMainThreadQueries()
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val migrate = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table AppWidgetBean (id INTEGER not null, baseType INTEGER not null, detailType INTEGER not null, PRIMARY KEY(id))")
            }

        }


    }

    abstract fun courseBaseDao(): CourseBaseDao

    abstract fun courseDetailDao(): CourseDetailDao

    abstract fun appWidgetDao(): AppWidgetDao

}