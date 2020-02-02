package com.suda.yzune.wakeupschedule.schedule

import android.os.Parcelable
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

class SchedulePagerAdapter(var maxWeek: Int, private val preLoad: Boolean, manager: FragmentManager) : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): ScheduleFragment {
        return ScheduleFragment.newInstance(position + 1, preLoad)
    }

    override fun getCount(): Int {
        return maxWeek
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun saveState(): Parcelable? {
        return null
    }

}