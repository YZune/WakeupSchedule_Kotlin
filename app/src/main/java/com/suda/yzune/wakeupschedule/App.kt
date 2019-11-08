package com.suda.yzune.wakeupschedule

import android.annotation.TargetApi
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
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
            Fabric.with(this, Crashlytics(), Answers())
        }
        PreferenceUtils.init(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelId = "schedule_reminder"
            var channelName = "课程提醒"
            var importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(this, channelId, channelName, importance)
            channelId = "news"
            channelName = "公告"
            importance = NotificationManager.IMPORTANCE_LOW
            createNotificationChannel(this, channelId, channelName, importance)
        }
        // todo: 6.0以后才允许设置。5.1上会导致WebView闪退。
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.setShowBadge(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}