package com.suda.yzune.wakeupschedule.course_add

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseDetailBean

class AddCourseAdapter(layoutResId: Int, data: MutableList<CourseDetailBean>) :
        BaseItemDraggableAdapter<CourseDetailBean, BaseViewHolder>(layoutResId, data) {

    private var mListener: OnItemEditTextChangedListener? = null
    val WEEK = arrayOf("", "周一", "周二", "周三", "周四", "周五", "周六", "周日")

    fun setListener(listener: OnItemEditTextChangedListener) {
        mListener = listener
    }

    override fun convert(helper: BaseViewHolder, item: CourseDetailBean) {
        //helper.setText(R.id.tv_item_id, "${helper.layoutPosition}")
        helper.setText(R.id.et_room, item.room)
        helper.setText(R.id.et_teacher, item.teacher)
        var type = ""
        when (item.type) {
            1 -> type = "单周"
            2 -> type = "双周"
        }
        helper.setText(R.id.et_weeks, "第${item.startWeek} - ${item.endWeek}周    $type")
        if (item.startNode != 0){
            helper.setText(R.id.et_time, "${WEEK[item.day]}    第${item.startNode} - ${item.startNode + item.step - 1}节")
        }
        helper.addOnClickListener(R.id.ib_delete)
        helper.addOnClickListener(R.id.ll_weeks)
        val etRoom = helper.getView<EditText>(R.id.et_room)
        val etTeacher = helper.getView<EditText>(R.id.et_teacher)
        etRoom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mListener?.onEditTextAfterTextChanged(s, helper.layoutPosition - 1, "room")
            }
        })

        etTeacher.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                mListener?.onEditTextAfterTextChanged(s, helper.layoutPosition - 1, "teacher")
            }
        })
    }

    interface OnItemEditTextChangedListener {
        fun onEditTextAfterTextChanged(editable: Editable, position: Int, what: String)
    }

}