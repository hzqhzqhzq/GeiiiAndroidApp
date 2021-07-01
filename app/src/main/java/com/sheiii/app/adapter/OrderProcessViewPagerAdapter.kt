package com.sheiii.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sheiii.app.model.AreaListModel
import com.sheiii.app.model.OrderTabs
import com.sheiii.app.view.order.OrderProcessFragment

/**
 * @author created by Zhenqing He on  17:18
 * @description
 */
class OrderProcessViewPagerAdapter(
    fa: FragmentManager,
    life: Lifecycle,
    data: MutableList<OrderTabs>
) : FragmentStateAdapter(fa, life) {
    private val data = data

    private lateinit var fragment: OrderProcessFragment

    override fun getItemCount(): Int {
        return data.size
    }

    override fun createFragment(position: Int): Fragment {
        fragment = OrderProcessFragment(data[position].orderStatus)
        return fragment
    }
}