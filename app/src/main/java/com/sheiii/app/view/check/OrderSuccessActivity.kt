package com.sheiii.app.view.check

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sheiii.app.BaseActivity
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.adapter.OrderSuccessRecommendAdapter
import com.sheiii.app.databinding.ActivityOrderSuccessBinding
import com.sheiii.app.model.Recommend
import com.sheiii.app.ui.BadgeActionProvider
import com.sheiii.app.ui.multistatepage.MyLoadingState
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.view.order.OrderDetailsActivity
import com.sheiii.app.viewmodel.OrderViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState


class OrderSuccessActivity : BaseActivity() {
    private var orderId = ""
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var binding: ActivityOrderSuccessBinding
    private lateinit var recommendListView: RecyclerView

    override fun init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_success)
        orderId = intent.getStringExtra("orderId").toString()
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        orderViewModel.getOrderSuccessRecommend()
        orderViewModel.getSuccessInfo(orderId)

        recommendListView = findViewById(R.id.order_success_recommed_listview)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_success
    }

    override fun setToolBar() {
        toolbar = binding.myToolbar

        toolbar.setNavigationOnClickListener {
            MainActivity.actionStart(this)
        }

        goCartIconView = MenuItemCompat.getActionProvider(toolbar.menu.getItem(0)) as BadgeActionProvider

        goCartIconView.setClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                MainActivity.actionStartCart(applicationContext, true)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        orderViewModel.orderInfo.observe(this, Observer {
            // 初始化下单成功数据
            initOrderSuccessInfo()

            multiStateContainer.show<SuccessState> { }
        })

        orderViewModel.recommendList.observe(this, Observer {
            // 配置推荐产品列表
            initRecommendList()
        })

        /* 前往订单详情 */
        findViewById<TextView>(R.id.go_order_details).setOnClickListener(object :
            PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                OrderDetailsActivity.actionStart(this@OrderSuccessActivity, orderId)
            }
        })

        /* 继续购物 */
        findViewById<TextView>(R.id.go_continue_shopping).setOnClickListener(object :
            PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                MainActivity.actionStart(this@OrderSuccessActivity)
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.actionStart(this)
    }

    /**
     * 初始化下单成功信息
     * @author Zhenqing He
     * @createDate 2021/6/23 12:05
     */
    private fun initOrderSuccessInfo() {
        binding.setVariable(BR.order_info, orderViewModel.orderInfo.value)
    }

    /**
     * 初始化推荐列表
     * @author Zhenqing He
     * @createDate 2021/6/23 12:06
     */
    private fun initRecommendList() {
        recommendListView.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = OrderSuccessRecommendAdapter(orderViewModel.recommendList.value?.list!!)
        }
    }

    private fun initRecommendListViewData(list: List<Recommend>): MutableList<MutableList<Recommend>> {
        var index = 0
        val SIZE = list.size / 3
        val resultList = mutableListOf<MutableList<Recommend>>()

        for (position in 0 until SIZE) {
            val tempList = mutableListOf<Recommend>()
            while (tempList.size < 3) {
                tempList.add(list[index])
                index++
            }
            resultList.add(tempList)
        }

        return resultList
    }

    private fun setListViewHeightBasedOnChildren(myListView: ListView?) {
        val adapter: ListAdapter = myListView!!.adapter
        var totalHeight = 0
        for (i in 0 until adapter.count) {
            val item: View = adapter.getView(i, null, myListView)
            item.measure(0, 0)
            totalHeight += item.measuredHeight
        }
        val params = myListView.layoutParams
        params.height = totalHeight + myListView.dividerHeight * (adapter.count - 1)
        myListView.layoutParams = params
    }

    companion object {
        fun actionStart(context: Context, orderId: String) {
            val intent = Intent(context, OrderSuccessActivity::class.java)
            intent.putExtra("orderId", orderId)
            context.startActivity(intent)
        }
    }
}