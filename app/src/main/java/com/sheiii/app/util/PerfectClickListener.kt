package com.sheiii.app.util

import android.view.View
import java.util.*

/**
 * 避免在1秒内触发多次点击
 * Created by Cazaea on 2016/1/15.
 */
abstract class PerfectClickListener : View.OnClickListener {
    private var id = -1
    private var lastClickTime: Long = 0
    override fun onClick(v: View) {
        val currentTime: Long = Calendar.getInstance().timeInMillis
        val mId: Int = v.id
        if (id != mId) {
            id = mId
            lastClickTime = currentTime
            onNoDoubleClick(v)
            return
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            onNoDoubleClick(v)
        }
    }

    protected abstract fun onNoDoubleClick(v: View?)

    companion object {
        private const val MIN_CLICK_DELAY_TIME = 1000
    }
}