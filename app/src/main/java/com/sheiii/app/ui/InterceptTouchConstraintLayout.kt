package com.sheiii.app.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * 拦截所有事件监听的ConstraintLayout，防止父级事件传递到子布局
 * @author Zhenqing He
 * @createDate 2021/4/14 14:59
 */
class InterceptTouchConstraintLayout(context: Context, attributeSet: AttributeSet) : ConstraintLayout(context, attributeSet) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return false
    }
}