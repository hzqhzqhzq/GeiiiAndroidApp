package com.sheiii.app.view.coupon

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sheiii.app.R
import com.sheiii.app.adapter.CouponViewPager2Adapter
import com.sheiii.app.constants.COUPON_EXPIRED
import com.sheiii.app.constants.COUPON_UNUSED
import com.sheiii.app.constants.COUPON_USED
import com.sheiii.app.model.Coupon
import com.sheiii.app.ui.CommonLoadingDialog
import com.sheiii.app.util.MyApplication
import com.sheiii.app.viewmodel.CouponViewModel

class CouponActivity : AppCompatActivity() {
    private lateinit var couponViewModel: CouponViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2



    private val couponMap: MutableMap<Int, List<Coupon>> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)
        couponViewModel = ViewModelProvider(this).get(CouponViewModel::class.java)
        tabLayout = findViewById(R.id.coupon_tablayout)
        viewPager2 = findViewById(R.id.coupon_viewpager2)

//        couponViewModel.getCouponPage(COUPON_UNUSED)


    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()

        initCouponPager()

    }

    /**
     * 初始化优惠券页面
     * @author Zhenqing He
     * @createDate 2021/4/23 13:43
     */
    private fun initCouponPager() {
        val couponData = couponViewModel.couponPager.value

        viewPager2.adapter = CouponViewPager2Adapter(supportFragmentManager, this.lifecycle, couponMap)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = resources.getString(R.string.unused)
                    tab.tag = COUPON_UNUSED
                }
                1 -> {
                    tab.text = resources.getString(R.string.expired)
                    tab.tag = COUPON_EXPIRED
                }
                2 -> {
                    tab.text = resources.getString(R.string.used)
                    tab.tag = COUPON_USED
                }
            }
        }.attach()
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, CouponActivity::class.java)
//            intent.putExtra("searchUrl", searchUrl)
            context.startActivity(intent)
        }
    }

}