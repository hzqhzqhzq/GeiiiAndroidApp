package com.sheiii.app.util

import android.view.View
import java.util.*

/**
 * @author created by Zhenqing He on  11:05
 * @description
 */
abstract class AuthCheckClickListener : View.OnClickListener {
    private var id = -1
    private var lastClickTime: Long = 0
    private val userAuth: UserAuth = UserAuth()
    override fun onClick(v: View) {
        val currentTime: Long = Calendar.getInstance().timeInMillis
        val mId: Int = v.id
        if (id != mId) {
            id = mId
            lastClickTime = currentTime
            if (userAuth.isLogin()) {
                onNoDoubleClick(v)
            }
            return
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            if (userAuth.isLogin()) {
                onNoDoubleClick(v)
            }
        }
    }

    protected abstract fun onNoDoubleClick(v: View?)

    companion object {
        private const val MIN_CLICK_DELAY_TIME = 1000
    }
}