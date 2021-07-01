package com.sheiii.app.view.account

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.Title
import com.sheiii.app.viewmodel.AccountViewModel

class SupportActivity : AppCompatActivity() {
    private lateinit var supportRecyclerView: RecyclerView
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        supportRecyclerView = findViewById(R.id.recyclerview_support)

        accountViewModel.getServiceInfo("contact")
    }

    override fun onResume() {
        super.onResume()
        accountViewModel.serviceInfo.observe(this, Observer {
            if (accountViewModel.serviceInfo.value != null) {
                // 初始化support 信息
                initSupportInfo()
            }
        })
    }

    private fun initSupportInfo() {
        supportRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter =
                CommonAdapter.Builder().setDatas(accountViewModel.serviceInfo.value?.titleList!! as List<*>)
                    .setLayoutId(R.layout.recycler_account_service_item)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            data as Title
                            var msgText = ""
                            for (msg in data.detailList) {
                                msgText = "${msgText}${msg}\n"
                            }
                            viewHolder.itemView.findViewById<TextView>(R.id.account_service_title).text = data.title
                            viewHolder.itemView.findViewById<TextView>(R.id.account_service_msg).text = msgText
                        }
                    }).create()
        }
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, SupportActivity::class.java)

            context.startActivity(intent)
        }
    }
}