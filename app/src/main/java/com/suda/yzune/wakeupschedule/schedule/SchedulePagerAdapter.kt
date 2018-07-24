package com.suda.yzune.wakeupschedule.schedule

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import java.util.ArrayList

class SchedulePagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    private val mFragmentList = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun removeAll() {
        mFragmentList.clear()
    }
}