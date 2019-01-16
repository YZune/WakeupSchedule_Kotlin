package com.suda.yzune.wakeupschedule

import android.os.Bundle
import android.view.View
import com.suda.yzune.wakeupschedule.base_view.BaseActivity
import com.suda.yzune.wakeupschedule.dao.TimeTableDao
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {

    private var job: Job? = null
    private lateinit var database: AppDatabase
    private lateinit var dao: TimeTableDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(application)
        dao = database.timeTableDao()

        fab.setOnClickListener {
            Toasty.success(applicationContext, "active: ${job?.isActive}    cancelled: ${job?.isCancelled}").show()
            if (job == null || !job!!.isActive) {
                job = loadData()
                job!!.start()
            } else {
                job?.cancel()
            }
        }
    }

    private fun loadData() = GlobalScope.launch(Dispatchers.Main, start = CoroutineStart.LAZY) {
        pb.visibility = View.VISIBLE
        tv_test.text = ""

        val task1 = async(Dispatchers.Main) {
            for (i in 0..100) {
                tv_test.text = i.toString()
                delay(1000)
            }
        }

        val task2 = async(Dispatchers.Main) {
            for (i in 0..100) {
                tv_test1.text = i.toString()
                delay(500)
            }
        }

        task1.await()
        task2.await()
        pb.visibility = View.GONE
        job?.cancel()
    }
}




