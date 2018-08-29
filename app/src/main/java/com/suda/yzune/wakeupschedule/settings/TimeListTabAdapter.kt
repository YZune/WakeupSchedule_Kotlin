package com.suda.yzune.wakeupschedule.settings

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class TimeListTabAdapter(private val fragmentManager: FragmentManager,
                         private val nodesNum: Int) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val isSummer = position != 0
        return TimeSettingsFragment.newInstance(nodesNum, isSummer)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "普通时间"
            else -> "夏令时"
        }
    }
}