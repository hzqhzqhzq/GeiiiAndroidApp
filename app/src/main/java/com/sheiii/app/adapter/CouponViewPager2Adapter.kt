package com.sheiii.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sheiii.app.constants.COUPON_EXPIRED
import com.sheiii.app.constants.COUPON_UNUSED
import com.sheiii.app.constants.COUPON_USED
import com.sheiii.app.model.Coupon
import com.sheiii.app.view.coupon.CouponViewpager2Fragment

/**
 * @author created by Zhenqing He on  13:47
 * @description
 */
class CouponViewPager2Adapter(
    fa: FragmentManager,
    lifecycle: Lifecycle,
    data: MutableMap<Int, List<Coupon>>
) :
    FragmentStateAdapter(fa, lifecycle) {
    private val couponMap = data

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
//        return when (position) {
            //            0 -> return couponMap[COUPON_UNUSED]?.let { CouponViewpager2Fragment(position, it) }!!
//            1 -> couponMap[COUPON_EXPIRED]?.let { CouponViewpager2Fragment(position, it) }!!
//            2 -> couponMap[COUPON_USED]?.let { CouponViewpager2Fragment(position, it) }!!
//            else -> couponMap[COUPON_UNUSED]?.let { CouponViewpager2Fragment(position, it) }!!
//        }
        return CouponViewpager2Fragment(position, null)
    }
}