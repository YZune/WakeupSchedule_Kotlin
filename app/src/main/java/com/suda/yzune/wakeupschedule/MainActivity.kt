package com.suda.yzune.wakeupschedule

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.arch.persistence.room.Room
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import android.os.AsyncTask
import android.util.Log
import com.suda.yzune.wakeupschedule.dao.CourseBaseDao


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.courseBaseDao()
        InsertAsyncTask(dao).execute()
    }
}

private class InsertAsyncTask internal constructor(private val mAsyncTaskDao: CourseBaseDao) : AsyncTask<CourseBaseBean, Void, Void>() {
    override fun doInBackground(vararg params: CourseBaseBean): Void? {

//        mAsyncTaskDao.insertCourseBase(CourseBaseBean(
//                id = 1,
//                courseName = "高等数学",
//                color = "",
//                tableName = "2018-2019 1"
//        ))

//        mAsyncTaskDao.getAll().forEach {
//            Log.d("数据库", it.toString())
//        }
        return null
    }
}
