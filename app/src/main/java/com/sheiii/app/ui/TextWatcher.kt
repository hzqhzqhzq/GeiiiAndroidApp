package com.sheiii.app.ui

import android.text.Editable
import android.widget.TextView


/**
 * @author created by Zhenqing He on  13:45
 * @description
 */
class MyTextWatcher(textView: TextView) : android.text.TextWatcher{
    private val text = textView

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        var num: Int = s!!.length
        num = 300 - num
        text.text = "$num/300"
    }
}