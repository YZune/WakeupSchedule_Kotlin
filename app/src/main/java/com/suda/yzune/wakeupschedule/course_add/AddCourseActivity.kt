package com.suda.yzune.wakeupschedule.course_add

import android.appwidget.AppWidgetManager
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_course.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.textColorResource


class AddCourseActivity : BaseTitleActivity(), AddCourseAdapter.OnItemEditTextChangedListener {

    override val layoutId: Int
        get() = R.layout.activity_add_course

    override fun onSetupSubButton(tvButton: TextView): TextView? {
        tvButton.text = "保存"
        tvButton.typeface = Typeface.DEFAULT_BOLD
        tvButton.textColorResource = R.color.colorAccent
        tvButton.setOnClickListener {
            if (viewModel.baseBean.courseName == "") {
                Toasty.error(this.applicationContext, "请填写课程名称").show()
            } else {
                if (viewModel.baseBean.id == -1 || !viewModel.updateFlag) {
                    launch {
                        val task = async(Dispatchers.IO) {
                            viewModel.checkSameName()
                        }
                        if (task.await() == null) {
                            saveData()
                        } else {
                            AddCourseTipFragment.newInstance().apply { isCancelable = false }.show(supportFragmentManager, "AddCourseTipFragment")
                        }
                    }
                } else {
                    saveData()
                }
            }
        }
        return tvButton
    }

    private lateinit var viewModel: AddCourseViewModel
    private lateinit var etName: EditText
    private var isExit: Boolean = false
    private val tExit = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            isExit = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AddCourseViewModel::class.java)

        if (intent.extras!!.getInt("id") == -1) {
            viewModel.tableId = intent.extras!!.getInt("tableId")
            viewModel.maxWeek = intent.extras!!.getInt("maxWeek")
            viewModel.nodes = intent.extras!!.getInt("nodes")
            initAdapter(AddCourseAdapter(R.layout.item_add_course_detail, viewModel.initData(viewModel.maxWeek)), viewModel.initBaseData())
        } else {
            viewModel.tableId = intent.extras!!.getInt("tableId")
            viewModel.maxWeek = intent.extras!!.getInt("maxWeek")
            viewModel.nodes = intent.extras!!.getInt("nodes")
            launch {
                val task1 = async(Dispatchers.IO) {
                    viewModel.initData(intent.extras!!.getInt("id"), viewModel.tableId)
                }
                val task2 = async(Dispatchers.IO) {
                    viewModel.initBaseData(intent.extras!!.getInt("id"))
                }
                task1.await().forEach {
                    viewModel.editList.add(CourseUtils.detailBean2EditBean(it))
                }
                val courseBaseBean = task2.await()
                viewModel.baseBean.id = courseBaseBean.id
                viewModel.baseBean.color = courseBaseBean.color
                viewModel.baseBean.courseName = courseBaseBean.courseName
                viewModel.baseBean.tableId = courseBaseBean.tableId
                initAdapter(AddCourseAdapter(R.layout.item_add_course_detail, viewModel.editList), viewModel.baseBean)
            }
        }
    }

    override fun onEditTextAfterTextChanged(editable: Editable, position: Int, what: String) {
        when (what) {
            "room" -> viewModel.editList[position].room = editable.toString()
            "teacher" -> viewModel.editList[position].teacher = editable.toString()
        }
    }

    private fun initAdapter(adapter: AddCourseAdapter, baseBean: CourseBaseBean) {
        adapter.setListener(this)
        adapter.addHeaderView(initHeaderView(baseBean))
        adapter.addFooterView(initFooterView(adapter))
        adapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.ll_time -> {
                    viewModel.editList[position].time.observe(this, Observer {
                        val textView = adapter.getViewByPosition(rv_detail, position + 1, R.id.et_time) as TextView
                        textView.text = "${CourseUtils.getDayStr(it!!.day)}    第${it.startNode} - ${it.endNode}节"
                    })
                    val selectTimeDialog = SelectTimeFragment.newInstance(position)
                    selectTimeDialog.isCancelable = false
                    selectTimeDialog.show(supportFragmentManager, "selectTime")
                }
                R.id.ib_delete -> {
                    if (adapter.data.size - viewModel.deleteList.size == 1) {
                        Toasty.error(this.applicationContext, "至少要保留一个时间段").show()
                    } else {
                        viewModel.deleteList.add(position)
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
                    viewModel.editList[position].weekList.observe(this, Observer {
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

    private fun initHeaderView(baseBean: CourseBaseBean): View {
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
            ivColor.setColorFilter(colorInt)
            tvColor.text = "点此更改颜色"
            tvColor.setTextColor(colorInt)
        }
        llColor.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("选取颜色")
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .initialColor(if (baseBean.color != "") tvColor.textColors.defaultColor else ContextCompat.getColor(applicationContext, R.color.red))
                    .lightnessSliderOnly()
                    .setPositiveButton("确定") { _, colorInt, _ ->
                        ivColor.setColorFilter(colorInt)
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
        val view = LayoutInflater.from(this).inflate(R.layout.item_add_course_btn, null)
        val tvBtn = view.findViewById<TextView>(R.id.tv_add)
        tvBtn.setOnClickListener {
            adapter.addData(CourseEditBean(
                    tableId = viewModel.tableId,
                    weekList = MutableLiveData<ArrayList<Int>>().apply {
                        this.value = ArrayList<Int>().apply {
                            for (i in 1..viewModel.maxWeek) {
                                this.add(i)
                            }
                        }
                    }))
        }
        return view
    }

    private fun saveData() {
        launch {
            val maxId = async(Dispatchers.IO) {
                viewModel.getLastId()
            }.await()

            if (viewModel.newId == -1) {
                if (maxId == null) {
                    viewModel.newId = 0
                } else {
                    viewModel.newId = maxId + 1
                }
            }

            val task = async(Dispatchers.IO) {
                try {
                    viewModel.preSaveData()
                    "ok"
                } catch (e: Exception) {
                    e.message
                }
            }
            val msg = task.await()
            when (msg) {
                "ok" -> {
                    launch {
                        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                        val list = async(Dispatchers.IO) {
                            viewModel.getScheduleWidgetIds()
                        }.await()
                        list.forEach {
                            when (it.detailType) {
                                0 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_schedule)
                                1 -> appWidgetManager.notifyAppWidgetViewDataChanged(it.id, R.id.lv_course)
                            }
                        }
                        Toasty.success(applicationContext, "保存成功").show()
                        finish()
                    }
                }
                "自身重复" -> {
                    Toasty.error(applicationContext, "此处填写的时间有重复，请仔细检查", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toasty.error(applicationContext, msg!!, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun exitBy2Click() {
        if (!isExit) {
            isExit = true // 准备退出
            ll_root.longSnackbar("真的不保存吗？那再按一次退出编辑哦，就不保存啦。", "退出编辑") { finish() }
            tExit.start() // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        exitBy2Click()  //退出应用的操作
    }

    override fun onDestroy() {
        super.onDestroy()
        tExit.cancel()
    }
}
