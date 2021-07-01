package com.sheiii.app.view.policy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sheiii.app.R
import com.sheiii.app.view.check.CheckActivity
import com.sheiii.app.viewmodel.PolicyViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState

class PolicyActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var policyViewModel: PolicyViewModel
    private lateinit var policyId: String

    private lateinit var multiStateContainer: MultiStateContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)
        policyId = intent.getStringExtra("policyId").toString()
        policyViewModel = ViewModelProvider(this).get(PolicyViewModel::class.java)

        multiStateContainer = MultiStatePage.bindMultiState(this, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                policyViewModel.getPolicy(policyId)
            }
        })
        multiStateContainer.show<LoadingState> { }

        webView = findViewById(R.id.policy_webview)

        policyViewModel.getPolicy(policyId)
    }

    override fun onResume() {
        super.onResume()
        policyViewModel.policy.observe(this, Observer {
            if (policyViewModel.policy.value != null) {
                // 初始化政策条款内容
                initPolicy()
            }
        })
    }

    private fun initPolicy() {
        val webSettings = webView.settings
//        webSettings.javaScriptEnabled = true

//        webSettings.useWideViewPort = true

        findViewById<TextView>(R.id.policy_title).text = policyViewModel.policy.value?.title

        webView.webViewClient = WebViewClient()

        webView.loadDataWithBaseURL(
            null,
            "<html><body>${policyViewModel.policy.value?.content}</body></html>",
            "text/html",
            "UTF-8",
            null
        )
        multiStateContainer.show<SuccessState> {  }
    }

    companion object {
        fun actionStart(context: Context, policyId: String) {
            val intent = Intent(context, PolicyActivity::class.java)
            intent.putExtra("policyId", policyId)
            context.startActivity(intent)
        }
    }
}