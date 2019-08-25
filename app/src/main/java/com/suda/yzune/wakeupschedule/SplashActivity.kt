package com.suda.yzune.wakeupschedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suda.yzune.wakeupschedule.main.MainActivity
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import org.jetbrains.anko.startActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UpdateUtils.tranOldData(applicationContext)
        startActivity<MainActivity>()
        //startActivity<SudaLifeActivity>("type" to "澡堂")
        finish()
    }

    override fun onBackPressed() {

    }
}
