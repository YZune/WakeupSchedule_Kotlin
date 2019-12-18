package com.suda.yzune.wakeupschedule.base_view

import android.widget.RemoteViewsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseRemoteViewsFactory : RemoteViewsService.RemoteViewsFactory, CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate() {
        job = Job()
    }

    override fun onDestroy() {
        job.cancel()
    }

}