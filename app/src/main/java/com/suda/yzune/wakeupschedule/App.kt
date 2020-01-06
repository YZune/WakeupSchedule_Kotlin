package com.suda.yzune.wakeupschedule

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.suda.yzune.wakeupschedule.schedule_settings.ScheduleSettingsActivity
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import es.dmoral.toasty.Toasty
import io.fabric.sdk.android.Fabric

class App : Application() {

    var activityCount = 0

    override fun onCreate() {
        super.onCreate()
        Toasty.Config.getInstance()
                .setToastTypeface(Typeface.DEFAULT_BOLD)
                .setTextSize(12)
                .apply()
        if (!BuildConfig.DEBUG) {
            AppCenter.start(this, "74cb13b8-bd94-40ce-99b3-c102cbadf772",
                    Analytics::class.java, Crashes::class.java)
        }
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
        if (PreferenceUtils.getBooleanFromSP(applicationContext, "s_night_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
                activityCount++
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
                activityCount--
                if (activity is ScheduleSettingsActivity && activityCount == 0) {
                    Toasty.info(applicationContext, "对小部件的编辑需要按「返回键」退出设置页面才能生效哦", Toast.LENGTH_LONG).show()
                }
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

        })
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