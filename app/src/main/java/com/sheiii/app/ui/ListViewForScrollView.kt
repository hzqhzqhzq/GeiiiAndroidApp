package com.sheiii.app.ui

import android.content.Context
import android.widget.ListView

/**
 * @author created by Zhenqing He on  15:03
 * @description
 */

class ListViewForScrollView(context: Context) : ListView(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val customSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, customSpec)
    }
}