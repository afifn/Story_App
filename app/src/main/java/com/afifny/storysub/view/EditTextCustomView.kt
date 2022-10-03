package com.afifny.storysub.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.afifny.storysub.R

class EditTextCustomView : AppCompatEditText {
    private var typeInput: Int = 0
    private lateinit var hintText: String
    private lateinit var errorText: String

    constructor(context: Context) : super(context) {
        init(attrs = null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    @SuppressLint("NewApi")
    private fun init(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.EditTextCustomView)
        typeInput = a.getInt(R.styleable.EditTextCustomView_type, 0)
        hintText = a.getString(R.styleable.EditTextCustomView_hint).toString()
        errorText = a.getString(R.styleable.EditTextCustomView_error).toString()

        when (typeInput) {
            1 -> {
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint = hintText
            }
            2 -> {
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                hint = hintText
            }
            3 -> {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                hint = hintText
            }
            else -> {
                inputType = InputType.TYPE_CLASS_TEXT
                hint = hintText
            }
        }
        a.recycle()
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (typeInput == 1) {
                    if (!p0.isNullOrEmpty() && p0.length < 6) {
                        error = errorText
                    }
                } else if (typeInput == 2) {
                    if (!p0.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(p0).matches()) {
                        error = errorText
                    }
                }
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (typeInput == 1) {
            transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
}
