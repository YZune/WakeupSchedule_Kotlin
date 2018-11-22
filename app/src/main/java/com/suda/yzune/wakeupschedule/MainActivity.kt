package com.suda.yzune.wakeupschedule

import android.os.Bundle
import android.util.Log
import android.view.View
import com.suda.yzune.wakeupschedule.dao.TimeTableDao
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {

    private lateinit var job: Job
    private lateinit var database: AppDatabase
    private lateinit var dao: TimeTableDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(application)
        dao = database.timeTableDao()

        job = loadData()

        fab.setOnClickListener {
            if (!job.isActive) {
                job.start()
            } else {
                job.cancel()
            }
            if (job.isCancelled) {
                job.start()
            }
        }
    }

    private fun loadData() = GlobalScope.launch(Dispatchers.Main, start = CoroutineStart.LAZY) {
        pb.visibility = View.VISIBLE
//        fab.isEnabled = false

        // 这是同时进行的(先读了再写？)
//        val task1 = async(Dispatchers.IO) {
//            Log.d("协程", "task1")
//            delay(2000)
//            for (i in 0..9) {
//                dao.insertTimeTable(TimeTableBean(0, ""))
//            }
//        }.await()

        val task2 = async(Dispatchers.IO) {
            Log.d("协程", "task2")
            delay(5000)
            dao.getMaxIdInThread()
        }.await()

        tv_test.text = "${task2}"
        pb.visibility = View.GONE
        Toasty.success(applicationContext, "${job.isCompleted}").show()
    }

}




