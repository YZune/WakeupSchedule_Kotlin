package com.suda.yzune.wakeupschedule

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.suda.yzune.wakeupschedule.schedule.ScheduleActivity
import com.suda.yzune.wakeupschedule.utils.UpdateUtils
import es.dmoral.toasty.Toasty
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.startActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
        UpdateUtils.tranOldData(applicationContext)
        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()
        startActivity<ScheduleActivity>()
        finish()
    }
}
