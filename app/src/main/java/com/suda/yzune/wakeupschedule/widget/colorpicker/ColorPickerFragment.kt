package com.suda.yzune.wakeupschedule.widget.colorpicker

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.fragment.app.BaseDialogFragment
import androidx.fragment.app.FragmentActivity
import com.suda.yzune.wakeupschedule.R
import kotlinx.android.synthetic.main.fragment_color_picker.*
import splitties.resources.color

class ColorPickerFragment : BaseDialogFragment(), ColorPickerView.OnColorChangedListener, TextWatcher {

    @ColorInt
    private var color: Int = 0
    private var showAlphaSlider = false
    private var fromEditText = false
    private var dialogId: Int = 0

    override val layoutId: Int
        get() = R.layout.fragment_color_picker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        showAlphaSlider = arguments!!.getBoolean("alpha")
        dialogId = arguments!!.getInt("id")
        color = savedInstanceState?.getInt("color") ?: arguments!!.getInt("color")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("color", color)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cpv_color.setAlphaSliderVisible(showAlphaSlider)
        cpv_color.setColor(color, true)
        cpv_color.setOnColorChangedListener(this)

        if (!showAlphaSlider) {
            et_color.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
        }

        view.setOnTouchListener { v, _ ->
            if (v != et_color && et_color.hasFocus()) {
                et_color.clearFocus()
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(et_color.windowToken, 0)
                et_color.clearFocus()
                return@setOnTouchListener true
            }
            false
        }

        setHex(color)
        v_color.setBackgroundColor(color)

        et_color.addTextChangedListener(this)

        et_color.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(et_color, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        btn_save.setOnClickListener {
            if (activity is ColorPickerDialogListener) {
                (activity as ColorPickerDialogListener).onColorSelected(dialogId, color)
                dismiss()
            } else {
                throw IllegalStateException("The activity must implement ColorPickerDialogListener")
            }
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setHex(color: Int) {
        if (showAlphaSlider) {
            et_color.setText(String.format("%08X", color))
        } else {
            et_color.setText(String.format("%06X", 0xFFFFFF and color))
        }
    }

    override fun onColorChanged(newColor: Int) {
        color = newColor
        if (!fromEditText) {
            setHex(newColor)
            if (et_color.hasFocus()) {
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(et_color.windowToken, 0)
                et_color.clearFocus()
            }
        }
        fromEditText = false
        v_color.setBackgroundColor(newColor)
    }

    override fun afterTextChanged(s: Editable?) {
        if (et_color.isFocused) {
            val color = try {
                parseColorString(s.toString())
            } catch (e: Exception) {
                color(R.color.colorAccent)
            }
            if (color != cpv_color.color) {
                fromEditText = true
                cpv_color.setColor(color, true)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    @Throws(NumberFormatException::class)
    private fun parseColorString(color: String): Int {
        var colorString = color
        val a: Int
        var r: Int
        val g: Int
        var b = 0
        if (colorString.startsWith("#")) {
            colorString = colorString.substring(1)
        }
        when {
            colorString.isEmpty() -> {
                r = 0
                a = 255
                g = 0
            }
            colorString.length <= 2 -> {
                a = 255
                r = 0
                b = Integer.parseInt(colorString, 16)
                g = 0
            }
            colorString.length == 3 -> {
                a = 255
                r = Integer.parseInt(colorString.substring(0, 1), 16)
                g = Integer.parseInt(colorString.substring(1, 2), 16)
                b = Integer.parseInt(colorString.substring(2, 3), 16)
            }
            colorString.length == 4 -> {
                a = 255
                r = Integer.parseInt(colorString.substring(0, 2), 16)
                g = r
                r = 0
                b = Integer.parseInt(colorString.substring(2, 4), 16)
            }
            colorString.length == 5 -> {
                a = 255
                r = Integer.parseInt(colorString.substring(0, 1), 16)
                g = Integer.parseInt(colorString.substring(1, 3), 16)
                b = Integer.parseInt(colorString.substring(3, 5), 16)
            }
            colorString.length == 6 -> {
                a = 255
                r = Integer.parseInt(colorString.substring(0, 2), 16)
                g = Integer.parseInt(colorString.substring(2, 4), 16)
                b = Integer.parseInt(colorString.substring(4, 6), 16)
            }
            colorString.length == 7 -> {
                a = Integer.parseInt(colorString.substring(0, 1), 16)
                r = Integer.parseInt(colorString.substring(1, 3), 16)
                g = Integer.parseInt(colorString.substring(3, 5), 16)
                b = Integer.parseInt(colorString.substring(5, 7), 16)
            }
            colorString.length == 8 -> {
                a = Integer.parseInt(colorString.substring(0, 2), 16)
                r = Integer.parseInt(colorString.substring(2, 4), 16)
                g = Integer.parseInt(colorString.substring(4, 6), 16)
                b = Integer.parseInt(colorString.substring(6, 8), 16)
            }
            else -> {
                b = -1
                g = -1
                r = -1
                a = -1
            }
        }
        return Color.argb(a, r, g, b)
    }

    companion object {
        fun newBuilder(): Builder {
            return Builder()
        }
    }

    interface ColorPickerDialogListener {
        fun onColorSelected(dialogId: Int, @ColorInt color: Int)
    }

    class Builder internal constructor() {

        @ColorInt
        private var color = Color.BLACK
        private var dialogId = 0
        private var showAlphaSlider = false

        fun setColor(color: Int): Builder {
            this.color = color
            return this
        }

        fun setShowAlphaSlider(showAlphaSlider: Boolean): Builder {
            this.showAlphaSlider = showAlphaSlider
            return this
        }

        fun setDialogId(dialogId: Int): Builder {
            this.dialogId = dialogId
            return this
        }

        /**
         * Create the [ColorPickerDialog] instance.
         *
         * @return A new [ColorPickerDialog].
         * @see .show
         */
        fun create(): ColorPickerFragment {
            val dialog = ColorPickerFragment()
            val args = Bundle()
            args.putInt("color", color)
            args.putBoolean("alpha", showAlphaSlider)
            args.putInt("id", dialogId)
            dialog.arguments = args
            return dialog
        }

        fun show(activity: FragmentActivity) {
            create().show(activity.supportFragmentManager, "color-picker-dialog")
        }
    }
}
