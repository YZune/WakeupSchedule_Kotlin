package com.suda.yzune.wakeupschedule.schedule_import

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener
import com.google.gson.Gson
import com.suda.yzune.wakeupschedule.AppDatabase
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_BNUZ
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_CF
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_HELP
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_HNIU
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_HNUST
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_JNU
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_LOGIN
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_PKU
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_QZ
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_QZ_BR
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_QZ_CRAZY
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_QZ_OLD
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_QZ_WITH_NODE
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_URP
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_URP_NEW
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_ZF
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_ZF_1
import com.suda.yzune.wakeupschedule.schedule_import.Common.TYPE_ZF_NEW
import com.suda.yzune.wakeupschedule.schedule_import.bean.SchoolInfo
import com.suda.yzune.wakeupschedule.utils.Const
import com.suda.yzune.wakeupschedule.utils.Utils
import com.suda.yzune.wakeupschedule.utils.getPrefer
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_school_list.*
import splitties.activities.start
import splitties.dimensions.dip
import splitties.resources.color
import splitties.resources.styledColor
import splitties.snackbar.action
import splitties.snackbar.longSnack

class SchoolListActivity : BaseTitleActivity(), OnQuickSideBarTouchListener {

    private val letters = HashMap<String, Int>()
    private val showList = arrayListOf<SchoolInfo>()
    private val schools = arrayListOf<SchoolInfo>()
    private lateinit var searchView: AppCompatEditText
    private var fromLocal = false

    override val layoutId: Int
        get() = R.layout.activity_school_list

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        tvButton.text = "申请适配"
        tvButton.setOnClickListener {
            start<LoginWebActivity> {
                putExtra("import_type", "apply")
            }
            finish()
        }
        return tvButton
    }

    override fun createTitleBar() = LinearLayoutCompat(this).apply {
        orientation = LinearLayoutCompat.VERTICAL
        setBackgroundColor(styledColor(R.attr.colorSurface))
        addView(LinearLayoutCompat(context).apply {
            setPadding(0, getStatusBarHeight(), 0, 0)
            setBackgroundColor(styledColor(R.attr.colorSurface))
            val outValue = TypedValue()
            context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)

            addView(AppCompatImageButton(context).apply {
                setImageResource(R.drawable.ic_back)
                setBackgroundResource(outValue.resourceId)
                setPadding(dip(8))
                setColorFilter(styledColor(R.attr.colorOnBackground))
                setOnClickListener {
                    onBackPressed()
                }
            }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(48)))

            mainTitle = AppCompatTextView(context).apply {
                text = title
                gravity = Gravity.CENTER_VERTICAL
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
            }

            addView(mainTitle, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(48)).apply {
                weight = 1f
            })

            searchView = AppCompatEditText(context).apply {
                hint = "请输入……"
                textSize = 16f
                background = null
                gravity = Gravity.CENTER_VERTICAL
                visibility = View.GONE
                setLines(1)
                setSingleLine()
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
                            longSnack("没有找到你的学校哦") {
                                action("申请适配") {
                                    start<LoginWebActivity> {
                                        putExtra("import_type", "apply")
                                    }
                                }
                            }
                        }
                    }

                })
            }

            addView(searchView, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(48)).apply {
                weight = 1f
            })

            val iconFont = ResourcesCompat.getFont(context, R.font.iconfont)
            addView(AppCompatTextView(context).apply {
                textSize = 20f
                typeface = iconFont
                text = "\uE6D4"
                gravity = Gravity.CENTER
                setBackgroundResource(outValue.resourceId)
                setOnClickListener {
                    when (searchView.visibility) {
                        View.GONE -> {
                            mainTitle.visibility = View.GONE
                            searchView.visibility = View.VISIBLE
                            setTextColor(color(R.color.colorAccent))
                            searchView.isFocusable = true
                            searchView.isFocusableInTouchMode = true
                            searchView.requestFocus()
                            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.showSoftInput(searchView, 0)
                        }
                    }
                }
            }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, dip(48)).apply {
                marginEnd = dip(24)
            })
        }, LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fromLocal = intent.getBooleanExtra("fromLocal", false)
        quickSideBarView.setOnQuickSideBarTouchListener(this)
        initSchoolList()
    }

    private fun initSchoolList() {
        val dataBase = AppDatabase.getDatabase(application)
        val tableDao = dataBase.tableDao()
        val gson = Gson()
        schools.apply {
            add(SchoolInfo("★", "如何正确选择教务类型？", "https://support.qq.com/embed/97617/faqs/59901", TYPE_HELP))
            getImportSchoolBean()?.let {
                it.sortKey = "★"
                add(it)
            }
            add(SchoolInfo("通", "新 URP 系统", "", TYPE_URP_NEW))
            add(SchoolInfo("通", "URP 系统", "", TYPE_URP))
            add(SchoolInfo("通", "新正方教务", "", TYPE_ZF_NEW))
            add(SchoolInfo("通", "正方教务", "", TYPE_ZF))
            add(SchoolInfo("通", "强智教务", "", TYPE_QZ))
            add(SchoolInfo("通", "旧强智（需要 IE 的那种）", "", TYPE_QZ_OLD))
            add(SchoolInfo("A", "安徽信息工程学院", "http://teach.aiit.edu.cn/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("A", "安徽农业大学", "http://newjwxt.ahau.edu.cn/jwglxt", TYPE_ZF_NEW))
            add(SchoolInfo("A", "安徽大学", "http://xk2.ahu.cn/default2.aspx", TYPE_ZF))
            add(SchoolInfo("A", "安徽工业大学", "http://jwxt.ahut.edu.cn/jsxsd/", TYPE_QZ))
            add(SchoolInfo("A", "安徽建筑大学", "http://219.231.0.156/", TYPE_ZF_NEW))
            add(SchoolInfo("A", "安徽财经大学", "", TYPE_URP_NEW))
            add(SchoolInfo("B", "保定学院", "http://jwgl.bdu.edu.cn/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("B", "北京信息科技大学", "http://jwgl.bistu.edu.cn/", TYPE_ZF))
            add(SchoolInfo("B", "北京化工大学", "http://jwglxt.buct.edu.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("B", "北京大学", "http://elective.pku.edu.cn", TYPE_PKU))
            add(SchoolInfo("B", "北京工业大学", "http://gdjwgl.bjut.edu.cn/", TYPE_ZF))
            add(SchoolInfo("B", "北京师范大学珠海分校", "http://es.bnuz.edu.cn/", TYPE_BNUZ))
            add(SchoolInfo("B", "北京林业大学", "http://newjwxt.bjfu.edu.cn/", TYPE_QZ_BR))
            add(SchoolInfo("B", "北京理工大学", "http://jwms.bit.edu.cn/", TYPE_QZ_WITH_NODE))
            add(SchoolInfo("B", "北京理工大学珠海学院", "http://e.zhbit.com/jsxsd/", TYPE_QZ_WITH_NODE))
            add(SchoolInfo("B", "北京联合大学", "", TYPE_ZF))
            add(SchoolInfo("B", "北京邮电大学", "https://jwxt.bupt.edu.cn/", TYPE_URP))
            add(SchoolInfo("B", "渤海大学", "http://jw.bhu.edu.cn/", TYPE_URP))
            add(SchoolInfo("B", "滨州医学院", "http://jwgl.bzmc.edu.cn/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("C", "常州机电职业技术学院", "http://jwc.czmec.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("C", "成都理工大学工程技术学院", "http://110.189.108.15/", TYPE_ZF))
            add(SchoolInfo("C", "重庆三峡学院", "http://jwgl.sanxiau.edu.cn/", TYPE_ZF))
            add(SchoolInfo("C", "重庆交通大学", "http://jwgl.cqjtu.edu.cn/jsxsd/", TYPE_QZ))
            add(SchoolInfo("C", "重庆交通职业学院", "", TYPE_ZF_1))
            add(SchoolInfo("C", "重庆大学城市科技学院", "", TYPE_QZ_CRAZY))
            add(SchoolInfo("C", "重庆邮电大学移通学院", "http://222.179.134.225:81/", TYPE_ZF))
            add(SchoolInfo("C", "长春大学", "http://cdjwc.ccu.edu.cn/jsxsd/", TYPE_QZ_BR))
            add(SchoolInfo("C", "长沙医学院", "http://jiaowu.csmu.edu.cn:8099/jsxsd/", TYPE_QZ_WITH_NODE))
            add(SchoolInfo("C", "长沙理工大学", "http://xk.csust.edu.cn/", TYPE_QZ_BR))
            add(SchoolInfo("D", "东北林业大学", "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/", TYPE_QZ))
            add(SchoolInfo("D", "东北财经大学", "http://202.199.165.159/", TYPE_URP))
            add(SchoolInfo("D", "东北石油大学", "http://jwgl.nepu.edu.cn/", TYPE_HNUST))
            add(SchoolInfo("D", "大庆师范学院", "", TYPE_QZ))
            add(SchoolInfo("D", "大连外国语大学", "http://cas.dlufl.edu.cn/cas/", TYPE_QZ))
            add(SchoolInfo("D", "大连大学", "http://202.199.155.33/default2.aspx", TYPE_ZF))
            add(SchoolInfo("D", "大连工业大学艺术与信息工程学院", "http://www.caie.org/page_556.shtml", TYPE_ZF))
            add(SchoolInfo("D", "德州学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("D", "电子科技大学中山学院", "http://jwgln.zsc.edu.cn/jsxsd/", TYPE_QZ))
            add(SchoolInfo("F", "佛山科学技术学院", "http://100.fosu.edu.cn/", TYPE_QZ_CRAZY))
            add(SchoolInfo("F", "福建农林大学", "http://jwgl.fafu.edu.cn", TYPE_ZF_1))
            add(SchoolInfo("F", "福建农林大学金山学院", "http://jsxyjwgl.fafu.edu.cn/", TYPE_ZF))
            add(SchoolInfo("F", "福建工程学院", "https://jwxtwx.fjut.edu.cn/jwglxt/", TYPE_ZF_NEW))
            add(SchoolInfo("F", "福建师范大学", "http://jwglxt.fjnu.edu.cn/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("G", "广东外语外贸大学", "http://jxgl.gdufs.edu.cn/jsxsd/", TYPE_QZ_WITH_NODE))
            add(SchoolInfo("G", "广东工业大学", "http://jxfw.gdut.edu.cn/", TYPE_CF))
            add(SchoolInfo("G", "广东海洋大学", "http://210.38.137.126:8016/default2.aspx", TYPE_ZF))
            add(SchoolInfo("G", "广东环境保护工程职业学院", "http://113.107.254.7/", TYPE_ZF))
            add(SchoolInfo("G", "广东科学技术职业学院", "", TYPE_ZF_1))
            add(SchoolInfo("G", "广东财经大学", "http://jwxt.gdufe.edu.cn/", TYPE_QZ))
            add(SchoolInfo("G", "广东金融学院", "http://jwxt.gduf.edu.cn/", TYPE_QZ_BR))
            add(SchoolInfo("G", "广州医科大学", "", TYPE_QZ_WITH_NODE))
            add(SchoolInfo("G", "广州大学", "", TYPE_ZF_NEW))
            add(SchoolInfo("G", "广西大学", "http://jwxt2018.gxu.edu.cn/jwglxt/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("G", "广西大学行健文理学院", "http://210.36.24.21:9017/jwglxt/xtgl", TYPE_ZF_NEW))
            add(SchoolInfo("G", "广西师范学院", "http://172.16.130.25/dean/student/login", TYPE_QZ))
            add(SchoolInfo("G", "硅湖职业技术学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("G", "贵州财经大学", "", TYPE_ZF_NEW))
            add(SchoolInfo("H", "华东理工大学", "https://inquiry.ecust.edu.cn/jsxsd/", TYPE_QZ))
            add(SchoolInfo("H", "华中农业大学", "http://jwgl.hzau.edu.cn/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("H", "华中师范大学", "http://one.ccnu.edu.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("H", "华中科技大学", "", TYPE_LOGIN))
            add(SchoolInfo("H", "华北电力大学科技学校", "http://202.204.74.178/", TYPE_ZF))
            add(SchoolInfo("H", "华南农业大学", "http://202.116.160.170/default2.aspx", TYPE_ZF))
            add(SchoolInfo("H", "华南理工大学", "http://xsjw2018.scuteo.com", TYPE_ZF_NEW))
            add(SchoolInfo("H", "哈尔滨商业大学", "http://jwxsd.hrbcu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("H", "哈尔滨工程大学", "", TYPE_QZ_CRAZY))
            add(SchoolInfo("H", "杭州医学院", "http://edu.hmc.edu.cn/", TYPE_ZF))
            add(SchoolInfo("H", "杭州电子科技大学", "http://jxgl.hdu.edu.cn/", TYPE_ZF))
            add(SchoolInfo("H", "河北大学", "http://zhjw.hbu.edu.cn/", TYPE_URP))
            add(SchoolInfo("H", "河北工程大学", "http://219.148.85.172:9111/login", TYPE_URP_NEW))
            add(SchoolInfo("H", "河北师范大学", "http://jwgl.hebtu.edu.cn/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("H", "河北政法职业学院", "http://jwxt.helc.edu.cn/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("H", "河北环境工程学院", "http://jw.hebuee.edu.cn/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("H", "河北科技师范学院", "http://121.22.25.47/", TYPE_ZF))
            add(SchoolInfo("H", "河北经贸大学", "http://222.30.218.44/default2.aspx", TYPE_ZF))
            add(SchoolInfo("H", "河北金融学院", "", TYPE_QZ_CRAZY))
            add(SchoolInfo("H", "河南工程学院", "http://125.219.48.18/", TYPE_ZF))
            add(SchoolInfo("H", "河南理工大学", "", TYPE_URP))
            add(SchoolInfo("H", "河南财经政法大学", "http://xk.huel.edu.cn/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("H", "河海大学", "http://202.119.113.135/", TYPE_URP))
            add(SchoolInfo("H", "海南大学", "http://jxgl.hainu.edu.cn/", TYPE_QZ_WITH_NODE))
            add(SchoolInfo("H", "海南师范大学", "http://210.37.0.16/", TYPE_ZF))
            add(SchoolInfo("H", "淮南师范学院", "http://211.70.176.173/jwglxt/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("H", "湖北中医药大学", "http://jwxt.hbtcm.edu.cn/jwglxt/xtgl", TYPE_ZF_NEW))
            add(SchoolInfo("H", "湖北医药学院", "http://jw.hbmu.edu.cn", TYPE_CF))
            add(SchoolInfo("H", "湖北工程学院新技术学院", "http://jwglxt.hbeutc.cn:20000/jwglxt/xtgl", TYPE_ZF_NEW))
            add(SchoolInfo("H", "湖北师范大学", "http://jwxt.hbnu.edu.cn/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("H", "湖北经济学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("H", "湖南信息职业技术学院", "http://my.hniu.cn/jwweb/ZNPK/KBFB_ClassSel.aspx", TYPE_HNIU))
            add(SchoolInfo("H", "湖南农业大学", "http://jwc.hunau.edu.cn/xsxk/", TYPE_ZF))
            add(SchoolInfo("H", "湖南商学院", "http://jwgl.hnuc.edu.cn/", TYPE_QZ))
            add(SchoolInfo("H", "湖南城市学院", "http://58.47.143.9:2045/zfca/login", TYPE_ZF))
            add(SchoolInfo("H", "湖南工业大学", "http://218.75.197.123:83/", TYPE_QZ))
            add(SchoolInfo("H", "湖南工商大学", "http://jwgl.hnuc.edu.cn/", TYPE_QZ))
            add(SchoolInfo("H", "湖南工学院", "http://jwgl.hnit.edu.cn/", TYPE_QZ_OLD))
            add(SchoolInfo("H", "湖南理工学院", "http://bkjw.hnist.cn/login", TYPE_URP))
            add(SchoolInfo("H", "湖南科技大学", "http://kdjw.hnust.cn:8080/kdjw", TYPE_HNUST))
            add(SchoolInfo("H", "湖南科技大学潇湘学院", "http://xxjw.hnust.cn:8080/xxjw/", TYPE_HNUST))
            add(SchoolInfo("H", "贺州学院", "http://jwglxt.hzu.gx.cn/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("H", "黄冈师范学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("H", "黑龙江外国语学院", "", TYPE_ZF))
            add(SchoolInfo("J", "吉林大学", "", TYPE_LOGIN))
            add(SchoolInfo("J", "吉林师范大学", "http://jwxt.jlnu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("J", "吉林建筑大学", "", TYPE_ZF_NEW))
            add(SchoolInfo("J", "吉首大学", "http://jwxt.jsu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("J", "嘉兴学院南湖学院", "http://jwzx.zjxu.edu.cn/jwglxt/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("J", "江苏工程职业技术学院", "http://tyjw.tmu.edu.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("J", "江苏师范大学", "http://sdjw.jsnu.edu.cn/", TYPE_QZ_WITH_NODE))
            add(SchoolInfo("J", "江苏建筑职业技术学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("J", "江苏科技大学", "http://jwgl.just.edu.cn:8080/jsxsd/", TYPE_QZ))
            add(SchoolInfo("J", "江西中医药大学", "http://jwxt.jxutcm.edu.cn/jwglxt/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("J", "江西农业大学南昌商学院", "http://223.83.249.67:8080/jsxsd/", TYPE_QZ_BR))
            add(SchoolInfo("J", "暨南大学", "https://jwxt.jnu.edu.cn/", TYPE_JNU))
            add(SchoolInfo("J", "济南大学", "http://jwgl4.ujn.edu.cn/jwglxt", TYPE_ZF_NEW))
            add(SchoolInfo("J", "济南工程职业技术学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("J", "锦州医科大学", "http://jwgl.jzmu.edu.cn/jsxsd/", TYPE_QZ))
            add(SchoolInfo("L", "临沂大学", "http://jwxt.lyu.edu.cn/jxd/", TYPE_QZ))
            add(SchoolInfo("L", "辽宁工业大学", "http://jwxt.lnut.edu.cn/default2.aspx", TYPE_ZF))
            add(SchoolInfo("L", "辽宁机电职业技术学院", "http://jwgl.lnjdp.com/", TYPE_ZF_NEW))
            add(SchoolInfo("M", "茂名职业技术学院", "http://jwc.mmvtc.cn/", TYPE_ZF_1))
            add(SchoolInfo("M", "闽南师范大学", "http://222.205.160.107/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("N", "内蒙古大学", "http://jwxt.imu.edu.cn/login", TYPE_URP_NEW))
            add(SchoolInfo("N", "内蒙古师范大学", "", TYPE_QZ))
            add(SchoolInfo("N", "内蒙古科技大学", "http://stuzhjw.imust.edu.cn/login", TYPE_URP_NEW))
            add(SchoolInfo("N", "内蒙古科技大学包头师范学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("N", "南京城市职业学院", "http://jw.ncc.edu.cn/jwglxt/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("N", "南京工业大学", "https://jwgl.njtech.edu.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("N", "南京师范大学中北学院", "http://222.192.5.246/", TYPE_ZF_NEW))
            add(SchoolInfo("N", "南京特殊教育师范学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("N", "南京理工大学", "http://202.119.81.112:8080/", TYPE_QZ))
            add(SchoolInfo("N", "南宁师范大学", "http://172.16.130.25/dean/student/login", TYPE_QZ))
            add(SchoolInfo("N", "南宁职业技术学院", "http://jwxt.ncvt.net:8088/jwglxt/", TYPE_ZF_NEW))
            add(SchoolInfo("N", "南方医科大学", "http://zhjw.smu.edu.cn/", TYPE_CF))
            add(SchoolInfo("N", "南方科技大学", "http://jwxt.sustc.edu.cn/jsxsd", TYPE_QZ))
            add(SchoolInfo("N", "南昌大学", "http://jwc104.ncu.edu.cn:8081/jsxsd/", TYPE_QZ))
            add(SchoolInfo("N", "南昌航空大学", "", TYPE_QZ))
            add(SchoolInfo("N", "宁波工程学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("Q", "清华大学", "", TYPE_LOGIN))
            add(SchoolInfo("Q", "青岛农业大学", "", TYPE_QZ_BR))
            add(SchoolInfo("Q", "青岛滨海学院", "http://jwgl.qdbhu.edu.cn/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("Q", "青岛科技大学", "https://jw.qust.edu.cn/jwglxt.htm", TYPE_ZF_NEW))
            add(SchoolInfo("Q", "齐鲁工业大学", "http://jwxt.qlu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("Q", "齐鲁师范学院", "", TYPE_URP_NEW))
            add(SchoolInfo("Q", "齐齐哈尔大学", "", TYPE_URP))
            add(SchoolInfo("S", "三江学院", "http://jw.sju.edu.cn/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("S", "上海大学", "", TYPE_LOGIN))
            add(SchoolInfo("S", "上海海洋大学", "https://urp.shou.edu.cn/login", TYPE_URP_NEW))
            add(SchoolInfo("S", "四川大学锦城学院", "http://jwweb.scujcc.cn/", TYPE_ZF))
            add(SchoolInfo("S", "四川美术学院", "", TYPE_QZ))
            add(SchoolInfo("S", "四川轻化工大学", "http://61.139.105.138/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("S", "山东农业大学", "http://xjw.sdau.edu.cn/jwglxt/", TYPE_ZF_NEW))
            add(SchoolInfo("S", "山东大学威海校区", "https://portal.wh.sdu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("S", "山东大学（威海）", "", TYPE_QZ))
            add(SchoolInfo("S", "山东师范大学", "http://www.bkjw.sdnu.edu.cn", TYPE_ZF))
            add(SchoolInfo("S", "山东政法大学", "http://114.214.79.176/jwglxt/", TYPE_ZF_NEW))
            add(SchoolInfo("S", "山东理工大学", "", TYPE_ZF_NEW))
            add(SchoolInfo("S", "山东科技大学", "http://jwgl.sdust.edu.cn/", TYPE_QZ))
            add(SchoolInfo("S", "山东财经大学", "", TYPE_QZ))
            add(SchoolInfo("S", "山东青年政治学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("S", "山西农业大学", "http://xsjwxt.sxau.edu.cn:7873/login", TYPE_URP_NEW))
            add(SchoolInfo("S", "山西工程技术学院", "http://211.82.48.36/login", TYPE_URP_NEW))
            add(SchoolInfo("S", "沈阳工程学院", "http://awcwea.com/jwgl.sie.edu.cn/jwgl/", TYPE_QZ))
            add(SchoolInfo("S", "沈阳师范大学", "http://210.30.208.140/", TYPE_ZF))
            add(SchoolInfo("S", "石家庄学院", "http://jwgl.sjzc.edu.cn/jwglxt/", TYPE_ZF_NEW))
            add(SchoolInfo("S", "绍兴文理学院", "http://jw.usx.edu.cn/", TYPE_ZF))
            add(SchoolInfo("S", "绍兴文理学院元培学院", "http://www.ypc.edu.cn/jwgl.htm", TYPE_ZF))
            add(SchoolInfo("S", "苏州农业职业技术学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("S", "苏州大学", "", TYPE_LOGIN))
            add(SchoolInfo("S", "苏州科技大学", "http://jw.usts.edu.cn/default2.aspx", TYPE_ZF))
            add(SchoolInfo("S", "苏州科技大学天平学院", "http://tpjw.usts.edu.cn/default2.aspx", TYPE_ZF))
            add(SchoolInfo("S", "韶关学院", "http://jwc.sgu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("T", "天津中医药大学", "http://jiaowu.tjutcm.edu.cn/jsxsd/", TYPE_QZ))
            add(SchoolInfo("T", "天津体育学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("T", "天津医科大学", "http://tyjw.tmu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("T", "天津工业大学", "http://jwpt.tjpu.edu.cn/", TYPE_URP))
            add(SchoolInfo("W", "五邑大学", "http://jxgl.wyu.edu.cn/", TYPE_CF))
            add(SchoolInfo("W", "威海职业学院", "", TYPE_QZ))
            add(SchoolInfo("W", "无锡太湖学院", "http://jwcnew.thxy.org/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("W", "武昌首义学院", "http://syjw.wsyu.edu.cn/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("W", "武汉东湖学院", "http://221.232.159.27/", TYPE_ZF))
            add(SchoolInfo("W", "武汉纺织大学", "", TYPE_ZF_NEW))
            add(SchoolInfo("W", "武汉轻工大学", "http://jwglxt.whpu.edu.cn/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("W", "温州医科大学", "http://jwxt.wmu.edu.cn", TYPE_ZF_NEW))
            add(SchoolInfo("W", "渭南师范学院", "http://218.195.46.9", TYPE_ZF))
            add(SchoolInfo("W", "潍坊学院", "http://210.44.64.154/", TYPE_ZF))
            add(SchoolInfo("W", "潍坊职业学院", "http://jwgl.sdwfvc.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("W", "皖西学院", "", TYPE_QZ))
            add(SchoolInfo("X", "信阳师范学院", "http://jwc.xynu.edu.cn/jxzhxxfwpt.htm", TYPE_ZF_NEW))
            add(SchoolInfo("X", "厦门工学院", "http://jwxt.xit.edu.cn/default2.aspx", TYPE_ZF))
            add(SchoolInfo("X", "厦门理工学院", "http://jw.xmut.edu.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("X", "徐州医科大学", "http://222.193.95.102/", TYPE_ZF_NEW))
            add(SchoolInfo("X", "徐州幼儿师范高等专科学校", "http://222.187.124.16/", TYPE_ZF))
            add(SchoolInfo("X", "湘潭大学", "http://jwxt.xtu.edu.cn/jsxsd/", TYPE_QZ))
            add(SchoolInfo("X", "西北工业大学", "", TYPE_LOGIN))
            add(SchoolInfo("X", "西华大学", "http://jwc.xhu.edu.cn/", TYPE_ZF))
            add(SchoolInfo("X", "西南大学", "", TYPE_ZF_NEW))
            add(SchoolInfo("X", "西南政法大学", "http://njwxt.swupl.edu.cn/jwglxt/xtgl", TYPE_ZF_NEW))
            add(SchoolInfo("X", "西南民族大学", "http://jwxt.swun.edu.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("X", "西南石油大学", "http://jwxt.swpu.edu.cn/", TYPE_URP))
            add(SchoolInfo("X", "西安外事学院", "http://jw.xaiu.edu.cn/", TYPE_ZF))
            add(SchoolInfo("X", "西安建筑科技大学", "http://xk.xauat.edu.cn/default2.aspx#a", TYPE_ZF))
            add(SchoolInfo("X", "西安理工大学", "http://202.200.112.200/", TYPE_ZF))
            add(SchoolInfo("X", "西安科技大学", "http://59.74.168.16:8989/", TYPE_ZF))
            add(SchoolInfo("X", "西安邮电大学", "http://www.zfjw.xupt.edu.cn/jwglxt/", TYPE_ZF_NEW))
            add(SchoolInfo("X", "西昌学院", "https://jwxt.xcc.edu.cn/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("Y", "云南财经大学", "http://202.203.194.2/", TYPE_ZF))
            add(SchoolInfo("Y", "延安大学", "http://jwglxt.yau.edu.cn/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("Y", "烟台大学", "http://xk.jwc.ytu.edu.cn/", TYPE_URP_NEW))
            add(SchoolInfo("Z", "中南大学", "https://csujwc.its.csu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("Z", "中南林业科技大学", "http://jwgl.csuft.edu.cn/", TYPE_QZ))
            add(SchoolInfo("Z", "中南财经政法大学", "", TYPE_QZ))
            add(SchoolInfo("Z", "中国农业大学", "http://urpjw.cau.edu.cn/login", TYPE_URP_NEW))
            add(SchoolInfo("Z", "中国医科大学", "http://jw.cmu.edu.cn/jwglxt/xtgl/login_slogin.html", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "中国地质大学（武汉）", "", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "中国石油大学（北京）", "http://urp.cup.edu.cn/login", TYPE_URP_NEW))
            add(SchoolInfo("Z", "中国矿业大学", "http://jwxt.cumt.edu.cn/jwglxt/", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "中国矿业大学徐海学院", "http://xhjw.cumt.edu.cn:8080/jwglxt/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "中国药科大学", "http://jwgl.cpu.edu.cn/", TYPE_QZ))
            add(SchoolInfo("Z", "浙江万里学院", "http://jwxt.zwu.edu.cn/", TYPE_ZF_1))
            add(SchoolInfo("Z", "浙江农林大学", "http://115.236.84.158/xtgl", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "浙江工业大学", "http://www.gdjw.zjut.edu.cn/", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "浙江工业大学之江学院", "http://jwgl.zzjc.edu.cn/default2.aspx", TYPE_ZF))
            add(SchoolInfo("Z", "浙江工商大学", "http://124.160.64.163/jwglxt/xtgl/", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "浙江师范大学", "", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "浙江师范大学行知学院", "", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "浙江财经大学", "http://fzjh.zufe.edu.cn/jwglxt", TYPE_ZF_NEW))
            add(SchoolInfo("Z", "郑州大学西亚斯国际学院", "http://218.198.176.111/default2.aspx", TYPE_ZF))
            add(SchoolInfo("Z", "郑州航空工业管理学院", "http://202.196.166.138/", TYPE_ZF))
        }

//        schools.sortWith(compareBy({ it.sortKey }, { it.name }))
//
//        schools.add(0, SchoolInfo("★", "URP 系统"))
//        schools.add(0, SchoolInfo("★", "旧强智（需要 IE 的那种）"))
//        schools.add(0, SchoolInfo("★", "强智教务"))
//        schools.add(0, SchoolInfo("★", "新正方教务"))
//        schools.add(0, SchoolInfo("★", "正方教务"))

        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        showList.addAll(schools)
        val adapter = SchoolImportListAdapter(R.layout.item_apply_info, showList)
        adapter.setOnItemClickListener { _, _, position ->
            if (showList[position].type == TYPE_HELP) {
                Utils.openUrl(this, showList[position].url)
                return@setOnItemClickListener
            }
            if (fromLocal) {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra("type", showList[position].type) })
                finish()
            } else {
                launch {
                    if (showList[position].type == Common.TYPE_MAINTAIN) {
                        Toasty.info(this@SchoolListActivity, "处于维护中哦").show()
                        return@launch
                    }
                    getPrefer().edit {
                        putString(Const.KEY_IMPORT_SCHOOL, gson.toJson(showList[position]))
                    }
                    val tableId = tableDao.getDefaultTableId()
                    startActivityForResult(Intent(this@SchoolListActivity, LoginWebActivity::class.java).apply {
                        putExtra("school_name", showList[position].name)
                        putExtra("import_type", showList[position].type)
                        putExtra("tableId", tableId)
                        putExtra("url", showList[position].url)
                    }, Const.REQUEST_CODE_IMPORT)
                }
            }
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

    private fun getImportSchoolBean(): SchoolInfo? {
        val json = getPrefer().getString(Const.KEY_IMPORT_SCHOOL, null)
                ?: return null
        val gson = Gson()
        val res = gson.fromJson<SchoolInfo>(json, SchoolInfo::class.java)
        if (!res.type.isNullOrEmpty()) {
            return gson.fromJson<SchoolInfo>(json, SchoolInfo::class.java)
        }
        return null
    }

    override fun onLetterTouching(touching: Boolean) {
        quickSideBarTipsView.visibility = if (touching) View.VISIBLE else View.INVISIBLE
    }

    override fun onLetterChanged(letter: String, position: Int, y: Float) {
        quickSideBarTipsView.setText(letter, position, y)
        if (letters.containsKey(letter)) {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(letters[letter]!!, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Const.REQUEST_CODE_IMPORT) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
