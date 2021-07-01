package com.sheiii.app.ui.multistatepage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.sheiii.app.R
import com.zy.multistatepage.MultiState
import com.zy.multistatepage.MultiStateContainer

/**
 * @author created by Zhenqing He on  18:00
 * @description
 */
class NetworkErrorState : MultiState() {
    private lateinit var tvLoadingMsg: TextView
    override fun onCreateMultiStateView(
        context: Context,
        inflater: LayoutInflater,
        container: MultiStateContainer
    ): View {
        return inflater.inflate(R.layout.multistate_network_error, container, false)
    }

    override fun onMultiStateViewCreate(view: View) {
//        tvLoadingMsg = view.findViewById(R.id.tv_loading_msg)
//        setLoadingMsg(MultiStatePage.config.loadingMsg)
    }

    override fun enableReload(): Boolean = true

//    fun setLoadingMsg(loadingMsg: String) {
//        tvLoadingMsg.text = loadingMsg
//    }
}