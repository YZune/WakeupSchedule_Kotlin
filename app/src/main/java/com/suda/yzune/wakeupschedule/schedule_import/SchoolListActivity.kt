package com.suda.yzune.wakeupschedule.schedule_import

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener
import com.suda.yzune.wakeupschedule.BaseTitleActivity
import com.suda.yzune.wakeupschedule.R
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_school_list.*


class SchoolListActivity : BaseTitleActivity(), OnQuickSideBarTouchListener {

    private val letters = HashMap<String, Int>()
    private val schools = arrayListOf<SchoolListBean>()

    override val layoutId: Int
        get() = R.layout.activity_school_list

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        tvButton.text = "申请适配"
        return tvButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quickSideBarView.setOnQuickSideBarTouchListener(this)
        initSchoolList()
    }

    private fun initSchoolList() {
        schools.add(SchoolListBean("S", "苏州大学"))
        schools.add(SchoolListBean("C", "长春大学"))
        schools.add(SchoolListBean("D", "大连外国语大学"))
        schools.add(SchoolListBean("T", "天津中医药大学"))
        schools.add(SchoolListBean("B", "北京林业大学"))

        schools.sortWith(compareBy({ it.sortKey }, { it.name }))

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = SchoolImportListAdapter(R.layout.item_apply_info, schools)
        adapter.setOnItemClickListener { _, _, position ->
            Toasty.success(applicationContext, "$position  ${schools[position].name}").show()
        }

        val customLetters = arrayListOf<String>()

        for ((position, school) in schools.withIndex()) {
            val letter = school.sortKey
            //如果没有这个key则加入并把位置也加入
            if (!letters.containsKey(letter)) {
                letters[letter] = position
                customLetters.add(letter)
            }
        }

        quickSideBarView.letters = customLetters
        recyclerView.adapter = adapter

        val headersDecor = StickyRecyclerHeadersDecoration(adapter)
        recyclerView.addItemDecoration(headersDecor)
    }

    override fun onLetterTouching(touching: Boolean) {
        quickSideBarTipsView.visibility = if (touching) View.VISIBLE else View.INVISIBLE
    }

    override fun onLetterChanged(letter: String, position: Int, y: Float) {
        quickSideBarTipsView.setText(letter, position, y)
        if (letters.containsKey(letter)) {
            recyclerView.scrollToPosition(letters[letter]!!)
        }
    }
}
