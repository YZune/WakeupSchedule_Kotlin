package com.suda.yzune.wakeupschedule.schedule_import

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.activity_school_list.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar


class SchoolListActivity : BaseTitleActivity(), OnQuickSideBarTouchListener {

    private val letters = HashMap<String, Int>()
    private val showList = arrayListOf<SchoolListBean>()
    private val schools = arrayListOf<SchoolListBean>()
    private lateinit var searchView: EditText

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

    override fun createTitleBar(): View {
        return UI {
            verticalLayout {
                backgroundColorResource = R.color.backgroundColor
                linearLayout {
                    topPadding = getStatusBarHeight()
                    backgroundColor = Color.WHITE
                    val outValue = TypedValue()
                    context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

                    imageButton(R.drawable.ic_back) {
                        backgroundResource = outValue.resourceId
                        padding = dip(8)
                        setOnClickListener {
                            onBackPressed()
                        }
                    }.lparams(wrapContent, dip(48))

                    mainTitle = textView(this@SchoolListActivity.title) {
                        gravity = Gravity.CENTER_VERTICAL
                        textSize = 16f
                        typeface = Typeface.DEFAULT_BOLD
                    }.lparams(wrapContent, dip(48)) {
                        weight = 1f
                    }

                    searchView = editText {
                        hint = "请输入……"
                        textSize = 16f
                        backgroundDrawable = null
                        gravity = Gravity.CENTER_VERTICAL
                        visibility = View.GONE
                        lines = 1
                        singleLine = true
                        imeOptions = EditorInfo.IME_ACTION_SEARCH
                        addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {}

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                showList.clear()
                                if (s.isNullOrBlank() || s.isEmpty()) {
                                    showList.addAll(schools)
                                } else {
                                    showList.addAll(schools.filter {
                                        it.name.contains(s.toString())
                                    })
                                }
                                recyclerView.adapter?.notifyDataSetChanged()
                                if (showList.isEmpty()) {
                                    longSnackbar("没有找到你的学校哦", "申请适配") { startActivity<LoginWebActivity>("type" to "apply") }
                                }
                            }

                        })
                    }.lparams(wrapContent, dip(48)) {
                        weight = 1f
                    }

                    val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
                    textView {
                        textSize = 20f
                        typeface = iconFont
                        text = "\uE6D4"
                        gravity = Gravity.CENTER
                        backgroundResource = outValue.resourceId
                        setOnClickListener {
                            when (searchView.visibility) {
                                View.GONE -> {
                                    mainTitle.visibility = View.GONE
                                    searchView.visibility = View.VISIBLE
                                    textColorResource = R.color.colorAccent
                                    searchView.isFocusable = true
                                    searchView.isFocusableInTouchMode = true
                                    searchView.requestFocus()
                                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                    inputMethodManager.showSoftInput(searchView, 0)
                                }
                            }
                        }
                    }.lparams(wrapContent, dip(48)) {
                        marginEnd = dip(24)
                    }
                }

                view {
                    backgroundColorResource = R.color.grey
                    alpha = 0.5f
                }.lparams(wrapContent, dip(1))
            }
        }.view
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
        schools.add(SchoolListBean("N", "南宁师范大学", "http://172.16.130.25/dean/student/login"))
        schools.add(SchoolListBean("H", "华东理工大学", "http://inquiry.ecust.edu.cn/"))
        schools.add(SchoolListBean("Z", "中南大学", "https://csujwc.its.csu.edu.cn/"))
        schools.add(SchoolListBean("H", "湖南商学院", "http://jwgl.hnuc.edu.cn/"))
        schools.add(SchoolListBean("S", "山东大学威海校区", "https://portal.wh.sdu.edu.cn/"))
        schools.add(SchoolListBean("J", "江苏师范大学", "http://sdjw.jsnu.edu.cn/"))
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
        schools.add(SchoolListBean("H", "河北师范大学", "http://jwgl.hebtu.edu.cn/xtgl/"))
        schools.add(SchoolListBean("G", "贵州财经大学", ""))
        schools.add(SchoolListBean("J", "江苏建筑职业技术学院", ""))
        schools.add(SchoolListBean("W", "武汉纺织大学", ""))
        schools.add(SchoolListBean("H", "湖南信息职业技术学院", "http://my.hniu.cn/jwweb/"))
        schools.add(SchoolListBean("Y", "云南财经大学", "http://202.203.194.2/"))
        schools.add(SchoolListBean("C", "重庆三峡学院", "http://jwgl.sanxiau.edu.cn/"))
        schools.add(SchoolListBean("H", "杭州电子科技大学", "http://jxgl.hdu.edu.cn/"))
        schools.add(SchoolListBean("B", "北京信息科技大学", "http://jwgl.bistu.edu.cn/"))
        schools.add(SchoolListBean("S", "绍兴文理学院", "http://jw.usx.edu.cn/"))
        schools.add(SchoolListBean("S", "山东政法大学", "http://114.214.79.176/jwglxt/"))
        schools.add(SchoolListBean("S", "石家庄学院", "http://jwgl.sjzc.edu.cn/jwglxt/"))
        schools.add(SchoolListBean("Z", "中国矿业大学", "http://jwxt.cumt.edu.cn/jwglxt/"))
        schools.add(SchoolListBean("W", "武汉轻工大学", "http://jwglxt.whpu.edu.cn/xtgl/"))
        schools.add(SchoolListBean("H", "黄冈师范学院", ""))
        schools.add(SchoolListBean("G", "广州大学", ""))
        schools.add(SchoolListBean("N", "南京师范大学中北学院", "http://222.192.5.246/"))
        schools.add(SchoolListBean("H", "湖北经济学院", ""))
        schools.add(SchoolListBean("H", "华中师范大学", "http://xk.ccnu.edu.cn/xtgl/"))
        schools.add(SchoolListBean("H", "华南理工大学", "http://xsjw2018.scuteo.com/driotlogin"))
        schools.add(SchoolListBean("G", "广东环境保护工程职业学院", "http://113.107.254.7/"))
        schools.add(SchoolListBean("X", "西华大学", "http://jwc.xhu.edu.cn/"))
        schools.add(SchoolListBean("X", "西安理工大学", "http://202.200.112.200/"))
        schools.add(SchoolListBean("C", "成都理工大学工程技术学院", "http://110.189.108.15/"))
        schools.add(SchoolListBean("L", "临沂大学", "http://jwxt.lyu.edu.cn/jxd/"))
        schools.add(SchoolListBean("X", "厦门理工学院", "http://jw.xmut.edu.cn/"))
        schools.add(SchoolListBean("B", "北京工业大学", "http://gdjwgl.bjut.edu.cn/"))
        schools.add(SchoolListBean("S", "绍兴文理学院元培学院", "http://www.ypc.edu.cn/jwgl.htm"))

        schools.add(SchoolListBean("D", "大连大学", "http://202.199.155.33/default2.aspx"))
        schools.add(SchoolListBean("H", "华南农业大学", "http://202.116.160.170/default2.aspx"))
        schools.add(SchoolListBean("H", "海南大学", "http://jxgl.hainu.edu.cn/"))
        schools.add(SchoolListBean("D", "大连工业大学艺术与信息工程学院", "http://www.caie.org/page_556.shtml"))
        schools.add(SchoolListBean("J", "吉林师范大学", "http://jwxt.jlnu.edu.cn/"))
        schools.add(SchoolListBean("D", "大庆师范学院", ""))
        schools.add(SchoolListBean("H", "哈尔滨商业大学", "http://jwxsd.hrbcu.edu.cn/"))
        schools.add(SchoolListBean("Q", "青岛农业大学", ""))
        schools.add(SchoolListBean("W", "潍坊学院", "http://210.44.64.154/"))
        schools.add(SchoolListBean("X", "湘潭大学", "http://jwxt.xtu.edu.cn/jsxsd/"))
        schools.add(SchoolListBean("Z", "中南林业科技大学", "http://jwgl.csuft.edu.cn/"))
        schools.add(SchoolListBean("G", "广州医科大学", ""))
        schools.add(SchoolListBean("G", "广东工业大学", "http://jxfw.gdut.edu.cn/"))
        schools.add(SchoolListBean("N", "南方医科大学", "http://zhjw.smu.edu.cn/"))
        schools.add(SchoolListBean("S", "山东财经大学", ""))
        schools.add(SchoolListBean("G", "广东金融学院", "http://jwxt.gduf.edu.cn/"))
        schools.add(SchoolListBean("C", "长沙医学院", "http://jwxt.gduf.edu.cn/"))
        schools.add(SchoolListBean("N", "南方科技大学", "http://jwxt.gduf.edu.cn/"))
        schools.add(SchoolListBean("W", "五邑大学", "http://jxgl.wyu.edu.cn/"))
        schools.add(SchoolListBean("H", "湖北医药学院", "http://jw.hbmu.edu.cn"))
        schools.add(SchoolListBean("H", "湖南工业大学", "http://218.75.197.123:83/"))
        schools.add(SchoolListBean("W", "潍坊职业学院", "http://jwgl.sdwfvc.cn/"))

        schools.add(SchoolListBean("A", "安徽工业大学", "http://211.70.149.135:88/"))
        schools.add(SchoolListBean("F", "福建师范大学", "http://jwgl.fjnu.edu.cn/"))

        schools.sortWith(compareBy({ it.sortKey }, { it.name }))

        schools.add(0, SchoolListBean("★", "强智教务"))
        schools.add(0, SchoolListBean("★", "新正方教务"))
        schools.add(0, SchoolListBean("★", "正方教务"))

        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        showList.addAll(schools)
        val adapter = SchoolImportListAdapter(R.layout.item_apply_info, showList)
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
