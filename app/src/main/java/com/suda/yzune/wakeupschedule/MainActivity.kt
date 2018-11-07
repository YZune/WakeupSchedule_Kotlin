package com.suda.yzune.wakeupschedule

import android.os.Bundle
import android.view.View
import com.suda.yzune.wakeupschedule.bean.TimeTableBean
import com.suda.yzune.wakeupschedule.dao.TimeTableDao
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database = AppDatabase.getDatabase(application)
        val dao = database.timeTableDao()

        fab.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                //                // 挂起当前上下文而非阻塞1000ms
//                for (i in 0..100) {
//                    tv_test.text = i.toString()
//                    delay(100)
//                }
//                tv_test.text = "done!"
                showIOData(dao)
            }
            pb.visibility = View.VISIBLE
            fab.hide()
        }
    }

    suspend fun showIOData(dao: TimeTableDao) {
        val deferred = GlobalScope.async(Dispatchers.IO) {
            dao.insertTimeTable(TimeTableBean(0, "test"))
            delay(2000)
            dao.getMaxIdInThread()
        }
        withContext(Dispatchers.Main) {
            val data = deferred.await()
            tv_test.text = data.toString()
            pb.visibility = View.GONE
            fab.show()
        }
    }

}




