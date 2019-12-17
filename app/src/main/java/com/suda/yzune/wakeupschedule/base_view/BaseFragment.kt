package com.suda.yzune.wakeupschedule.base_view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = lifecycle.coroutineScope.coroutineContext

}