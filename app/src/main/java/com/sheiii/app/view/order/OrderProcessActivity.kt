package com.sheiii.app.view.order

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sheiii.app.BaseActivity
import com.sheiii.app.R
import com.sheiii.app.adapter.OrderProcessViewPagerAdapter
import com.sheiii.app.view.check.CheckActivity
import com.sheiii.app.viewmodel.OrderViewModel

class OrderProcessActivity : BaseActivity() {
    private lateinit var process: String
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var orderTabLayout: TabLayout
    private lateinit var orderViewPager: ViewPager2

    override fun init() {
        process = intent.getStringExtra("process").toString()
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        orderViewModel.getOrderTabs(process)

        orderTabLayout = findViewById(R.id.order_process_tab)
        orderViewPager = findViewById(R.id.order_process_viewpager)

        orderViewModel.orderTabsList.observe(this, Observer {
            if (orderViewModel.orderTabsList.value != null) {
                // 初始化订单的tabs
                initOrderTabs()
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_process
    }

    override fun setToolBar() { }

    private fun initOrderTabs() {
        orderViewPager.isSaveEnabled = false
        orderViewPager.adapter = OrderProcessViewPagerAdapter(supportFragmentManager, lifecycle, orderViewModel.orderTabsList.value!!)

        TabLayoutMediator(orderTabLayout, orderViewPager) { tab, position ->
            tab.text = orderViewModel.orderTabsList.value?.get(position)?.tabName
            tab.tag = orderViewModel.orderTabsList.value?.get(position)?.orderStatus
        }.attach()

        var showPosition = 0

        for ((index, tab) in orderViewModel.orderTabsList.value!!.withIndex()) {
            if (tab.orderStatus == process) {
                showPosition = index
                break
            }
        }

        orderTabLayout.getTabAt(showPosition)?.select()
        orderViewPager.currentItem = showPosition

    }

    companion object {
        fun actionStart(context: Context, process: String) {
            val intent = Intent(context, OrderProcessActivity::class.java)
            intent.putExtra("process", process)
            context.startActivity(intent)
        }
    }
}