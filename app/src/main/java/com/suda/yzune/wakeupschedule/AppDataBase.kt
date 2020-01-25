package com.suda.yzune.wakeupschedule

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.suda.yzune.wakeupschedule.bean.*
import com.suda.yzune.wakeupschedule.dao.*

@Database(entities = [CourseBaseBean::class, CourseDetailBean::class, AppWidgetBean::class, TimeDetailBean::class,
    TimeTableBean::class, TableBean::class],
        version = 8, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, "wakeup")
                                .allowMainThreadQueries()
                                .addMigrations(migration7to8)
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val migration7to8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE TimeTableBean (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL);")
                database.execSQL("INSERT INTO TimeTableBean VALUES(1, '默认');")
                database.execSQL("CREATE TABLE TableBean (\n" +
                        "    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                        "    tableName TEXT NOT NULL, \n" +
                        "    nodes INTEGER NOT NULL DEFAULT 11, \n" +
                        "    background TEXT NOT NULL DEFAULT '',\n" +
                        "    timeTable INTEGER NOT NULL DEFAULT 1,\n" +
                        "    startDate TEXT NOT NULL DEFAULT '2019-02-25',\n" +
                        "    maxWeek INTEGER NOT NULL DEFAULT 30,\n" +
                        "    itemHeight INTEGER NOT NULL DEFAULT 56,\n" +
                        "    itemAlpha INTEGER NOT NULL DEFAULT 60,\n" +
                        "    itemTextSize INTEGER NOT NULL DEFAULT 12,\n" +
                        "    widgetItemHeight INTEGER NOT NULL DEFAULT 56,\n" +
                        "    widgetItemAlpha INTEGER NOT NULL DEFAULT 60,\n" +
                        "    widgetItemTextSize INTEGER NOT NULL DEFAULT 12,\n" +
                        "    strokeColor INTEGER NOT NULL DEFAULT 0x80ffffff,\n" +
                        "    widgetStrokeColor INTEGER NOT NULL DEFAULT 0x80ffffff,\n" +
                        "    textColor INTEGER NOT NULL DEFAULT 0xff000000,\n" +
                        "    widgetTextColor INTEGER NOT NULL DEFAULT 0xff000000,\n" +
                        "    courseTextColor INTEGER NOT NULL DEFAULT 0xff000000,\n" +
                        "    widgetCourseTextColor INTEGER NOT NULL DEFAULT 0xff000000,\n" +
                        "    showSat INTEGER NOT NULL DEFAULT 1,\n" +
                        "    showSun INTEGER NOT NULL DEFAULT 1,\n" +
                        "    sundayFirst INTEGER NOT NULL DEFAULT 0,\n" +
                        "    showOtherWeekCourse INTEGER NOT NULL DEFAULT 0,\n" +
                        "    showTime INTEGER NOT NULL DEFAULT 0,\n" +
                        "    type INTEGER NOT NULL DEFAULT 0,\n" +
                        "    FOREIGN KEY (timeTable) REFERENCES TimeTableBean (id) ON DELETE SET DEFAULT ON UPDATE CASCADE\n" +
                        ");")
                database.execSQL("CREATE INDEX index_TableBean_id_timeTable ON TableBean (timeTable ASC);")
                database.execSQL("ALTER TABLE CourseBaseBean RENAME TO CourseBaseBean_old;")
                database.execSQL("CREATE TABLE CourseBaseBean(id INTEGER NOT NULL, courseName TEXT NOT NULL, color TEXT NOT NULL, tableId INTEGER NOT NULL, PRIMARY KEY (id, tableId), FOREIGN KEY (tableId) REFERENCES TableBean (id) ON DELETE CASCADE ON UPDATE CASCADE);")
                database.execSQL("INSERT INTO TableBean (tableName) VALUES('');")
                database.execSQL("INSERT INTO TableBean (tableName) VALUES('情侣课表');")
                database.execSQL("INSERT INTO CourseBaseBean (id, courseName, color, tableId) SELECT id, courseName, color, CASE WHEN tableName = '' THEN 1 ELSE 2 END FROM CourseBaseBean_old;")
                database.execSQL("CREATE INDEX index_CourseBaseBean_tableId ON CourseBaseBean (tableId ASC);")
                database.execSQL("DROP TABLE CourseBaseBean_old;")
                database.execSQL("DROP INDEX index_CourseDetailBean_id_tableName;")
                database.execSQL("ALTER TABLE CourseDetailBean RENAME TO CourseDetailBean_old;")
                database.execSQL("CREATE TABLE CourseDetailBean (\n" +
                        "  id INTEGER NOT NULL,\n" +
                        "  day INTEGER NOT NULL,\n" +
                        "  room TEXT,\n" +
                        "  teacher TEXT,\n" +
                        "  startNode INTEGER NOT NULL,\n" +
                        "  step INTEGER NOT NULL,\n" +
                        "  startWeek INTEGER NOT NULL,\n" +
                        "  endWeek INTEGER NOT NULL,\n" +
                        "  type INTEGER NOT NULL,\n" +
                        "  tableId INTEGER NOT NULL,\n" +
                        "  PRIMARY KEY (day, startNode, startWeek, type, tableId, id),\n" +
                        "  FOREIGN KEY (\"id\", \"tableId\") REFERENCES \"CourseBaseBean\" (\"id\", \"tableId\") ON DELETE CASCADE ON UPDATE CASCADE\n" +
                        ");")
                database.execSQL("INSERT INTO CourseDetailBean (id, day, room, teacher, startNode, step, startWeek, endWeek, type, tableId) SELECT id, day, room, teacher, startNode, step, startWeek, endWeek, type, CASE WHEN tableName = '' THEN 1 ELSE 2 END FROM CourseDetailBean_old;")
                database.execSQL("CREATE INDEX index_CourseDetailBean_id_tableId ON CourseDetailBean (id ASC, tableId ASC);")
                database.execSQL("DROP TABLE CourseDetailBean_old")

                database.execSQL("ALTER TABLE TimeDetailBean RENAME TO TimeDetailBean_old;")
                database.execSQL("CREATE TABLE TimeDetailBean (node INTEGER NOT NULL, startTime TEXT NOT NULL, endTime TEXT NOT NULL, timeTable INTEGER NOT NULL DEFAULT 1, PRIMARY KEY (node, timeTable), FOREIGN KEY (timeTable) REFERENCES TimeTableBean (id) ON DELETE CASCADE ON UPDATE CASCADE);")
                database.execSQL("INSERT INTO TimeDetailBean (node, startTime, endTime) SELECT node, startTime, endTime FROM TimeDetailBean_old;")
                database.execSQL("CREATE INDEX index_TimeDetailBean_id_timeTable ON TimeDetailBean(timeTable ASC);")
                database.execSQL("DROP TABLE TimeDetailBean_old;")
                database.execSQL("ALTER TABLE TimeTableBean ADD COLUMN sameLen INTEGER NOT NULL DEFAULT 1;")
                database.execSQL("ALTER TABLE TimeTableBean ADD COLUMN courseLen INTEGER NOT NULL DEFAULT 50;")
            }
        }
    }

    abstract fun courseDao(): CourseDao

    abstract fun appWidgetDao(): AppWidgetDao

    abstract fun timeTableDao(): TimeTableDao

    abstract fun timeDetailDao(): TimeDetailDao

    abstract fun tableDao(): TableDao
}