package com.sheiii.app.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.sheiii.app.GlideApp
import com.sheiii.app.R
import com.sheiii.app.util.MyApplication
import com.sheiii.app.viewmodel.ActiveViewModel

class ActivityActivity : AppCompatActivity() {
    private lateinit var activeViewModel: ActiveViewModel

    //    private lateinit var banner: Banner<Image, BannerImageAdapter<Image>>
    private lateinit var banner: ImageView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity)

        activeViewModel = ViewModelProvider(this).get(ActiveViewModel::class.java)
        activeViewModel.getPromotionData()

        banner = findViewById(R.id.active_banner)
        tabLayout = findViewById(R.id.active_tablayout)
        viewPager2 = findViewById(R.id.active_viewpager2)
    }

    override fun onResume() {
        super.onResume()

        activeViewModel.promotionData.observe(this, Observer {
            if (activeViewModel.promotionData.value != null) {
                initPromotionData()
            }
        })
    }

    /**
     * null
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/27 15:37
     */
    private fun initPromotionData() {
        GlideApp.with(banner)
            .load(MyApplication.getImageHost() + activeViewModel.promotionData.value?.promotionData?.imgUrl)
            .into(banner)

    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, ActivityActivity::class.java)
            context.startActivity(intent)
        }
    }
}