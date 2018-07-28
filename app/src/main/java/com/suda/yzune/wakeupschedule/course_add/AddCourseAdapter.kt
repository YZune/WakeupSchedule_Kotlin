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

    fun setListener(listener: OnItemEditTextChangedListener) {
        mListener = listener
    }

    override fun convert(helper: BaseViewHolder, item: CourseDetailBean) {
        helper.setText(R.id.tv_item_id, "${helper.layoutPosition}")
        helper.setText(R.id.et_room, item.room)
        helper.setText(R.id.et_teacher, item.teacher)
        helper.addOnClickListener(R.id.ib_delete)
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