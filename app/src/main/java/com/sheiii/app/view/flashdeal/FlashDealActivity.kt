package com.sheiii.app.view.flashdeal

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import cn.iwgang.countdownview.CountdownView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sheiii.app.R
import com.sheiii.app.adapter.FlashDealViewpager2Adapter
import com.sheiii.app.view.category.CategoryActivity
import com.sheiii.app.viewmodel.FlashDealViewModel

class FlashDealActivity : AppCompatActivity() {
    private lateinit var flashDealViewModel: FlashDealViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewpager2: ViewPager2
    private lateinit var countDown: CountdownView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_deal)
        flashDealViewModel = ViewModelProvider(this).get(FlashDealViewModel::class.java)
        flashDealViewModel.getFlashPage()
        flashDealViewModel.getPromotionData()

        tabLayout = findViewById(R.id.flash_deal_tabLayout)
        viewpager2 = findViewById(R.id.flash_deal_viewpager2)
        countDown = findViewById(R.id.flash_deal_countdown)
    }

    override fun onResume() {
        super.onResume()

        flashDealViewModel.flashPage.observe(this, Observer {
            if (flashDealViewModel.flashPage.value != null) {
                initCountDown()
            }
        })

        flashDealViewModel.promotionData.observe(this, Observer {
            if (flashDealViewModel.promotionData.value != null) {
                initTabLayout()
            }
        })

    }

    /**
     * null
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/26 16:10
     */
    private fun initCountDown() {
        val flashDealData = flashDealViewModel.flashPage.value
        countDown.start(flashDealData?.leftSeconds?.toLong()!! * 1000)

        val titleDesc = findViewById<TextView>(R.id.flash_deal_title_desc)
        titleDesc.text = flashDealData.titleDesc
    }

    /**
     * null
     * @url null
     * @method null
     * @param null
     * @author Zhenqing He
     * @createDate 2021/4/26 18:10
     */
    private fun initTabLayout() {
        val promotionData = flashDealViewModel.promotionData.value
        viewpager2.adapter = FlashDealViewpager2Adapter(supportFragmentManager, this.lifecycle, promotionData?.categoryData!!)
        TabLayoutMediator(tabLayout, viewpager2) { tab, position ->
            tab.text = promotionData.categoryData[position].title
        }.attach()
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, FlashDealActivity::class.java)
//            intent.putExtra("searchUrl", searchUrl)
            context.startActivity(intent)
        }
    }
}