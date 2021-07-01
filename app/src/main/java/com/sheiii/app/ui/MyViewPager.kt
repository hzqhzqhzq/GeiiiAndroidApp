package com.sheiii.app.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.viewpager.widget.ViewPager

/**
 * @author created by Zhenqing He on  09:00
 * @description
 */
class MyViewPager(context: Context) : ViewPager(context) {

    constructor(context: Context, attributeSet: AttributeSet) : this(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = child.measuredHeight
            if (h > height) height = h
        }

        val myHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, myHeightMeasureSpec)
    }
}