package com.sheiii.app.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author created by Zhenqing He on  15:22
 * @description
 */
class NoScrollViewPager(context: Context, attr: AttributeSet) : ViewPager(context, attr) {
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}