package com.suda.yzune.wakeupschedule.schedule

class SchedulePagerAdapter(var maxWeek: Int, manager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(manager) {

    override fun getItem(position: Int): ScheduleFragment {
        return ScheduleFragment.newInstance(position + 1)
    }

    override fun getCount(): Int {
        return maxWeek
    }

    override fun getItemPosition(`object`: Any): Int {
        return androidx.viewpager.widget.PagerAdapter.POSITION_NONE
    }
}