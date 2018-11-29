package com.suda.yzune.wakeupschedule.schedule_import

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.activity_school_list.*
import org.jetbrains.anko.startActivity


class SchoolListActivity : BaseTitleActivity(), OnQuickSideBarTouchListener {

    private val letters = HashMap<String, Int>()
    private val schools = arrayListOf<SchoolListBean>()

    override val layoutId: Int
        get() = R.layout.activity_school_list

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        tvButton.text = "申请适配"
        tvButton.setOnClickListener {
            startActivity<LoginWebActivity>("type" to "apply")
            finish()
        }
        return tvButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quickSideBarView.setOnQuickSideBarTouchListener(this)
        initSchoolList()
    }

    private fun initSchoolList() {
        val dataBase = AppDatabase.getDatabase(application)
        val tableDao = dataBase.tableDao()

        schools.add(SchoolListBean("S", "苏州大学"))
        schools.add(SchoolListBean("G", "广东外语外贸大学", "http://jxgl.gdufs.edu.cn/jsxsd/"))
        schools.add(SchoolListBean("C", "长春大学", "http://cdjwc.ccu.edu.cn/jsxsd/"))
        schools.add(SchoolListBean("D", "大连外国语大学", "http://cas.dlufl.edu.cn/cas/"))
        schools.add(SchoolListBean("T", "天津中医药大学", "http://jiaowu.tjutcm.edu.cn/jsxsd/"))
        schools.add(SchoolListBean("B", "北京林业大学", "http://newjwxt.bjfu.edu.cn/"))
        schools.add(SchoolListBean("J", "锦州医科大学", "http://jwgl.jzmu.edu.cn/jsxsd/"))
        schools.add(SchoolListBean("S", "山东科技大学", "http://jwgl.sdust.edu.cn/"))
        schools.add(SchoolListBean("Z", "中国药科大学", "http://jwgl.cpu.edu.cn/"))
        schools.add(SchoolListBean("G", "广西师范学院", "http://172.16.130.25/dean/student/login"))
        schools.add(SchoolListBean("H", "华东理工大学", "http://inquiry.ecust.edu.cn/"))
        schools.add(SchoolListBean("Z", "中南大学", "https://csujwc.its.csu.edu.cn/"))
        schools.add(SchoolListBean("H", "湖南商学院", "http://jwgl.hnuc.edu.cn/"))
        schools.add(SchoolListBean("S", "山东大学威海校区", "https://portal.wh.sdu.edu.cn/"))
        schools.add(SchoolListBean("J", "江苏师范大学", ""))
        schools.add(SchoolListBean("J", "吉首大学", "http://jwxt.jsu.edu.cn/"))
        schools.add(SchoolListBean("N", "南京理工大学", "http://202.119.81.112:8080/"))
        schools.add(SchoolListBean("T", "天津医科大学", "http://tyjw.tmu.edu.cn/"))
        schools.add(SchoolListBean("C", "重庆交通大学", "http://jwgl.cqjtu.edu.cn/jsxsd/"))
        schools.add(SchoolListBean("S", "沈阳工程学院", "http://awcwea.com/jwgl.sie.edu.cn/jwgl/"))
        schools.add(SchoolListBean("S", "韶关学院", "http://jwc.sgu.edu.cn/"))
        schools.add(SchoolListBean("Z", "中南财经政法大学", ""))
        schools.add(SchoolListBean("W", "威海职业学院", ""))
        schools.add(SchoolListBean("Z", "中南林业科技大学", "http://54.222.196.251:81/znlykjdxswxy_jsxsd/"))
        schools.add(SchoolListBean("D", "东北林业大学", "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/"))
        schools.add(SchoolListBean("Q", "齐鲁工业大学", "http://jwxt.qlu.edu.cn/"))
        schools.add(SchoolListBean("S", "四川美术学院", ""))
        schools.add(SchoolListBean("G", "广东财经大学", "http://jwxt.gdufe.edu.cn/"))
        schools.add(SchoolListBean("N", "南昌航空大学", ""))
        schools.add(SchoolListBean("W", "皖西学院", ""))
        schools.add(SchoolListBean("Z", "浙江师范大学行知学院", ""))
        schools.add(SchoolListBean("Z", "浙江师范大学", ""))
        schools.add(SchoolListBean("G", "硅湖职业技术学院", ""))
        schools.add(SchoolListBean("X", "西南民族大学", "http://jwxt.swun.edu.cn/"))
        schools.add(SchoolListBean("S", "山东理工大学", ""))
        schools.add(SchoolListBean("J", "江苏工程职业技术学院", "http://tyjw.tmu.edu.cn/"))
        schools.add(SchoolListBean("N", "南京工业大学", "https://jwgl.njtech.edu.cn/"))
        schools.add(SchoolListBean("D", "德州学院", ""))
        schools.add(SchoolListBean("N", "南京特殊教育师范学院", ""))
        schools.add(SchoolListBean("J", "济南工程职业技术学院", ""))
        schools.add(SchoolListBean("J", "吉林建筑大学", ""))
        schools.add(SchoolListBean("N", "宁波工程学院", ""))
        schools.add(SchoolListBean("X", "西南大学", ""))
        schools.add(SchoolListBean("H", "河北师范大学", ""))
        schools.add(SchoolListBean("G", "贵州财经大学", ""))
        schools.add(SchoolListBean("J", "江苏建筑职业技术学院", ""))
        schools.add(SchoolListBean("W", "武汉纺织大学", ""))
        schools.add(SchoolListBean("H", "湖南信息职业技术学院", "http://my.hniu.cn/jwweb/"))

        schools.sortWith(compareBy({ it.sortKey }, { it.name }))

        schools.add(0, SchoolListBean("★", "新正方教务"))
        schools.add(0, SchoolListBean("★", "正方教务"))

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = SchoolImportListAdapter(R.layout.item_apply_info, schools)
        adapter.setOnItemClickListener { _, _, position ->
            tableDao.getDefaultTableId().observe(this, Observer {
                startActivity<LoginWebActivity>(
                        "type" to schools[position].name,
                        "tableId" to it,
                        "url" to schools[position].url
                )
                finish()
            })
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
