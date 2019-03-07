package com.suda.yzune.wakeupschedule.course_add

import android.appwidget.AppWidgetManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseListActivity
import com.suda.yzune.wakeupschedule.bean.CourseBaseBean
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import com.suda.yzune.wakeupschedule.widget.colorpicker.ColorPickerFragment
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar


class AddCourseActivity : BaseListActivity(), ColorPickerFragment.ColorPickerDialogListener, AddCourseAdapter.OnItemEditTextChangedListener {

    private lateinit var tvColor: TextView
    private lateinit var ivColor: TextView

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
                        val textView = adapter.getViewByPosition(mRecyclerView, position + 1, R.id.et_time) as TextView
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
                        val viewHolder = mRecyclerView.findViewHolderForLayoutPosition(position + 1)
                        if (viewHolder != null) {
                            val lp = viewHolder.itemView.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams
                            lp.height = 0
                            lp.bottomMargin = 0
                            viewHolder.itemView.layoutParams = lp
                        }
                    }
                }
                R.id.ll_weeks -> {
                    viewModel.editList[position].weekList.observe(this, Observer {
                        it!!.sort()
                        val textView = adapter.getViewByPosition(mRecyclerView, position + 1, R.id.et_weeks) as TextView
                        val text = CourseUtils.intList2WeekBeanList(it).toString()
                        textView.text = text.substring(1, text.length - 1)
                    })
                    val selectWeekDialog = SelectWeekFragment.newInstance(position)
                    selectWeekDialog.isCancelable = false
                    selectWeekDialog.show(supportFragmentManager, "selectWeek")
                }
            }
        }
        mRecyclerView.adapter = adapter
        mRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    }

    private fun initHeaderView(baseBean: CourseBaseBean): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_add_course_base, null)
        etName = view.find(R.id.et_name)
        val rlRoot = view.find<RelativeLayout>(R.id.rl_root)
        rlRoot.topPadding = getStatusBarHeight() + dip(48)
        val llColor = view.findViewById<LinearLayout>(R.id.ll_color)
        tvColor = view.findViewById(R.id.tv_color)
        ivColor = view.findViewById(R.id.iv_color)
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
            ivColor.textColor = colorInt
            tvColor.text = "点此更改颜色"
            tvColor.setTextColor(colorInt)
        }
        llColor.setOnClickListener {
            ColorPickerFragment.newBuilder()
                    .setColor(if (baseBean.color != "") tvColor.textColors.defaultColor else ContextCompat.getColor(applicationContext, R.color.red))
                    .setShowAlphaSlider(false)
                    .show(this)
        }
        return view
    }

    override fun onColorSelected(dialogId: Int, colorInt: Int) {
        ivColor.textColor = colorInt
        tvColor.text = "点此更改颜色"
        tvColor.setTextColor(colorInt)
        viewModel.baseBean.color = "#${Integer.toHexString(colorInt)}"
    }

    private fun initFooterView(adapter: AddCourseAdapter): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_add_course_btn, null)
        val tvBtn = view.findViewById<TextView>(R.id.tv_add)
        tvBtn.setOnClickListener {
            adapter.addData(CourseEditBean(
                    teacher = viewModel.editList[0].teacher,
                    room = viewModel.editList[0].room,
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
            val maxId = withContext(Dispatchers.IO) {
                viewModel.getLastId()
            }

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
                        val list = withContext(Dispatchers.IO) {
                            viewModel.getScheduleWidgetIds()
                        }
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
            mRecyclerView.longSnackbar("真的不保存吗？那再按一次退出编辑哦，就不保存啦。", "退出编辑") { finish() }
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
