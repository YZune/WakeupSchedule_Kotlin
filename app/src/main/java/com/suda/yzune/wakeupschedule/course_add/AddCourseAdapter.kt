package com.suda.yzune.wakeupschedule.course_add

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils

class AddCourseAdapter(layoutResId: Int, data: MutableList<CourseEditBean>) :
        BaseQuickAdapter<CourseEditBean, BaseViewHolder>(layoutResId, data) {

    private var mListener: OnItemEditTextChangedListener? = null

    fun setListener(listener: OnItemEditTextChangedListener) {
        mListener = listener
    }

    override fun convert(helper: BaseViewHolder, item: CourseEditBean) {
        //helper.setText(R.id.tv_item_id, "${helper.layoutPosition}")
        helper.setText(R.id.et_room, item.room)
        helper.setText(R.id.et_teacher, item.teacher)

        val week = CourseUtils.intList2WeekBeanList(item.weekList.value!!).toString()
        helper.setText(R.id.et_weeks, week.substring(1, week.length - 1))

        helper.setText(R.id.et_time, "${CourseUtils.getDayStr(item.time.value!!.day)}    第${item.time.value!!.startNode} - ${item.time.value!!.endNode}节")

        helper.addOnClickListener(R.id.ib_delete)
        helper.addOnClickListener(R.id.ll_weeks)
        helper.addOnClickListener(R.id.ll_time)
        helper.addOnClickListener(R.id.ll_teacher)
        helper.addOnClickListener(R.id.ll_room)
    }

    interface OnItemEditTextChangedListener {
        fun onEditTextAfterTextChanged(editable: Editable, position: Int, what: String)
    }

    override fun onDetachedFromRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mListener = null
    }

}