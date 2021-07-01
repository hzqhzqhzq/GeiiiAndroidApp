package com.sheiii.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sheiii.app.model.CategoryData
import com.sheiii.app.model.Coupon
import com.sheiii.app.view.coupon.CouponViewpager2Fragment
import com.sheiii.app.view.flashdeal.FlashDealViewpager2Fragment

/**
 * @author created by Zhenqing He on  09:15
 * @description
 */
class FlashDealViewpager2Adapter(
    fa: FragmentManager,
    lifecycle: Lifecycle,
    data: List<CategoryData>
) : FragmentStateAdapter(fa, lifecycle) {
    val list = data
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return FlashDealViewpager2Fragment()
    }
}
