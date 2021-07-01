package com.sheiii.app.ui.multistatepage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.sheiii.app.R
import com.zy.multistatepage.MultiState
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage

/**
 * @author created by Zhenqing He on  16:43
 * @description
 */
class MyLoadingState : MultiState() {
    private lateinit var tvLoadingMsg: TextView
    override fun onCreateMultiStateView(
        context: Context,
        inflater: LayoutInflater,
        container: MultiStateContainer
    ): View {
        return inflater.inflate(R.layout.multistate_loading, container, false)
    }

    override fun onMultiStateViewCreate(view: View) {
//        tvLoadingMsg = view.findViewById(R.id.tv_loading_msg)
//        setLoadingMsg(MultiStatePage.config.loadingMsg)
    }

//    fun setLoadingMsg(loadingMsg: String) {
//        tvLoadingMsg.text = loadingMsg
//    }
}