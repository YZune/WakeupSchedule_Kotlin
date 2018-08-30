package com.suda.yzune.wakeupschedule.course_add

import android.appwidget.AppWidgetManager
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.utils.AppWidgetUtils
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils
import com.suda.yzune.wakeupschedule.utils.ViewUtils.createColorStateList
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_course.*


class AddCourseActivity : AppCompatActivity(), AddCourseAdapter.OnItemEditTextChangedListener {

    private lateinit var viewModel: AddCourseViewModel
    private lateinit var etName: EditText
    private var isExit: Boolean = false
    private var isSaved = false
    private val tExit = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            isExit = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ViewUtils.fullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        ViewUtils.resizeStatusBar(this, v_status)

        viewModel = ViewModelProviders.of(this).get(AddCourseViewModel::class.java)
        viewModel.initRepository(applicationContext)

        viewModel.getLastId().observe(this, Observer {
            if (viewModel.newId == -1) {
                if (it != null) {
                    viewModel.newId = it + 1
                } else {
                    viewModel.newId = 0
                }
            }
        })

        if (intent.extras == null) {
            initAdapter(AddCourseAdapter(R.layout.item_add_course_detail, viewModel.initData(PreferenceUtils.getIntFromSP(this.applicationContext, "sb_weeks", 30).toLong())), viewModel.initBaseData())
        } else {
            viewModel.initData(intent.extras.getInt("id")).observe(this, Observer { list ->
                //viewModel.getList().clear()
                if (!isSaved) {
                    list!!.forEach {
                        viewModel.getList().add(CourseUtils.detailBean2EditBean(it))
                    }
                    viewModel.initBaseData(intent.extras.getInt("id")).observe(this, Observer {
                        viewModel.getBaseData().id = it!!.id
                        viewModel.getBaseData().color = it.color
                        viewModel.getBaseData().courseName = it.courseName
                        viewModel.getBaseData().tableName = it.tableName
                        initAdapter(AddCourseAdapter(R.layout.item_add_course_detail, viewModel.getList()), viewModel.getBaseData())
                    })
                }
            })
        }

        initEvent()
    }

    override fun onEditTextAfterTextChanged(editable: Editable, position: Int, what: String) {
        when (what) {
            "room" -> viewModel.getList()[position].room = editable.toString()
            "teacher" -> viewModel.getList()[position].teacher = editable.toString()
        }
    }

    private fun initAdapter(adapter: AddCourseAdapter, baseBean: CourseBaseBean) {
        adapter.setListener(this)
        adapter.addHeaderView(initHeaderView(adapter, baseBean))
        adapter.addFooterView(initFooterView(adapter))
        adapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ll_time -> {
                    viewModel.getList()[position].time.observe(this, Observer {
                        val textView = adapter.getViewByPosition(rv_detail, position + 1, R.id.et_time) as TextView
                        textView.text = "${CourseUtils.getDayInt(it!!.day)}    第${it.startNode} - ${it.endNode}节"
                    })
                    val selectTimeDialog = SelectTimeFragment.newInstance(position)
                    selectTimeDialog.isCancelable = false
                    selectTimeDialog.show(supportFragmentManager, "selectTime")
                }
                R.id.ib_delete -> {
                    if (adapter.data.size - viewModel.getDeleteList().size == 1) {
                        Toasty.error(this.applicationContext, "至少要保留一个时间段").show()
                    } else {
                        viewModel.getDeleteList().add(position)
                        val viewHolder = rv_detail.findViewHolderForLayoutPosition(position + 1)
                        if (viewHolder != null) {
                            val lp = viewHolder.itemView.layoutParams as RecyclerView.LayoutParams
                            lp.height = 0
                            lp.bottomMargin = 0
                            viewHolder.itemView.layoutParams = lp
                        }
                    }
                }
                R.id.ll_weeks -> {
                    viewModel.getList()[position].weekList.observe(this, Observer {
                        it!!.sort()
                        val textView = adapter.getViewByPosition(rv_detail, position + 1, R.id.et_weeks) as TextView
                        val text = CourseUtils.intList2WeekBeanList(it).toString()
                        textView.text = text.substring(1, text.length - 1)
                    })
                    val selectWeekDialog = SelectWeekFragment.newInstance(position)
                    selectWeekDialog.isCancelable = false
                    selectWeekDialog.show(supportFragmentManager, "selectWeek")
                }
            }
        }
        rv_detail.adapter = adapter
        rv_detail.layoutManager = LinearLayoutManager(this)
    }

    private fun initHeaderView(adapter: AddCourseAdapter, baseBean: CourseBaseBean): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_add_course_base, null)
        etName = view.findViewById<EditText>(R.id.et_name)
        val llColor = view.findViewById<LinearLayout>(R.id.ll_color)
        val tvColor = view.findViewById<TextView>(R.id.tv_color)
        val ivColor = view.findViewById<ImageView>(R.id.iv_color)
        etName.setText(baseBean.courseName)
        etName.setSelection(baseBean.courseName.length)
        etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                baseBean.courseName = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        tvColor.text = baseBean.color
        if (baseBean.color != "") {
            val colorInt = Color.parseColor(baseBean.color)
            ivColor.imageTintList = createColorStateList(colorInt, colorInt, colorInt, colorInt)
            tvColor.text = "点此更改颜色"
            tvColor.setTextColor(colorInt)
        }
        llColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("选取颜色")
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .initialColor(if (baseBean.color != "") tvColor.textColors.defaultColor else resources.getColor(R.color.red))
                    .lightnessSliderOnly()
                    .setPositiveButton("确定") { _, colorInt, _ ->
                        ivColor.imageTintList = createColorStateList(colorInt, colorInt, colorInt, colorInt)
                        tvColor.text = "点此更改颜色"
                        tvColor.setTextColor(colorInt)
                        baseBean.color = "#${Integer.toHexString(colorInt)}"
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .build()
                    .show()
        }
        return view
    }

    private fun initFooterView(adapter: AddCourseAdapter): View {
        val weeksNum = PreferenceUtils.getIntFromSP(this.applicationContext, "sb_weeks", 30)
        val view = LayoutInflater.from(this).inflate(R.layout.item_add_course_btn, null)
        val cvBtn = view.findViewById<CardView>(R.id.cv_add)
        cvBtn.setOnClickListener {
            adapter.addData(CourseEditBean(weekList = MutableLiveData<ArrayList<Int>>().apply {
                this.value = ArrayList<Int>().apply {
                    for (i in 1..weeksNum) {
                        this.add(i)
                    }
                }
            }))
        }
        return view
    }

    private fun initEvent() {
        tv_cancel.setOnClickListener {
            finish()
        }

        tv_save.setOnClickListener { _ ->
            if (viewModel.getBaseData().courseName == "") {
                Toasty.error(this.applicationContext, "请填写课程名称").show()
            } else {
                if (viewModel.getBaseData().id == -1 || !viewModel.getUpdateFlag()) {
                    viewModel.checkSameName().observe(this, Observer {
                        if (it == null) {
                            saveData()
                        } else if (!isSaved) {
                            Toasty.error(this.applicationContext, "已存在相同名称的课程，请编辑该课程，然后添加不同的时间段").show()
                        }
                    })
                } else {
                    saveData()
                }
            }
        }
    }

    private fun saveData() {
        viewModel.saveData()
        viewModel.getSaveInfo().observe(this, Observer { s ->
            when (s) {
                "ok" -> {
                    Toasty.success(this.applicationContext, "保存成功").show()
                    isSaved = true
                    val appWidgetManager = AppWidgetManager.getInstance(this.applicationContext)
                    viewModel.getWidgetIds().forEach {
                        AppWidgetUtils.refreshScheduleWidget(this.applicationContext, appWidgetManager, it)
                    }
                    finish()
                }
                "其他重复" -> {
                    Toasty.error(this.applicationContext, "插入异常，请确保时间与已有课程时间没有冲突", Toast.LENGTH_LONG).show()
                }
                "自身重复" -> {
                    Toasty.error(this.applicationContext, "此处填写的时间有重复，请仔细检查", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toasty.error(this.applicationContext, s!!, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun exitBy2Click() {
        if (!isExit) {
            isExit = true // 准备退出
            Toasty.info(this.applicationContext, "再按一次退出编辑").show()
            tExit.start() // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click()  //退出应用的操作
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        tExit.cancel()
    }
}
