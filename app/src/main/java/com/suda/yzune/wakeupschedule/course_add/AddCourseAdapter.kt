package com.suda.yzune.wakeupschedule.course_add

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.bean.CourseEditBean
import com.suda.yzune.wakeupschedule.utils.CourseUtils
import kotlinx.android.synthetic.main.activity_add_course.*

class AddCourseAdapter(layoutResId: Int, data: MutableList<CourseEditBean>) :
        BaseItemDraggableAdapter<CourseEditBean, BaseViewHolder>(layoutResId, data) {

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

        helper.setText(R.id.et_time, "${CourseUtils.getDayInt(item.time.value!!.day)}    第${item.time.value!!.startNode} - ${item.time.value!!.endNode}节")

        helper.addOnClickListener(R.id.ib_delete)
        helper.addOnClickListener(R.id.ll_weeks)
        helper.addOnClickListener(R.id.ll_time)
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