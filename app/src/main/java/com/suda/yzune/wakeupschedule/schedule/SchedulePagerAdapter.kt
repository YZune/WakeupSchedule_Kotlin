package com.suda.yzune.wakeupschedule.schedule

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

class SchedulePagerAdapter(var maxWeek: Int, manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

    override fun getItem(position: Int): ScheduleFragment {
        return ScheduleFragment.newInstance(position + 1)
    }

    override fun getCount(): Int {
        return maxWeek
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}