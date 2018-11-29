package com.suda.yzune.wakeupschedule

import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import es.dmoral.toasty.Toasty
import io.fabric.sdk.android.Fabric

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()
        if (!BuildConfig.DEBUG && !Fabric.isInitialized()) {
            Fabric.with(this, Crashlytics())
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}