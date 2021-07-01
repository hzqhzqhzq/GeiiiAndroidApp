package com.sheiii.app.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.sheiii.app.model.HomeTabModel
import com.sheiii.app.view.home.HomeTabChildFragment

/**
 * @author created by Zhenqing He on  15:30
 * @description
 */
class HomeTabFragmentAdapter(fa: Fragment, tabList: MutableList<HomeTabModel>) : FragmentStateAdapter(fa){
    private val tabList = tabList
    private val fa = fa

    override fun getItemCount(): Int = tabList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = HomeTabChildFragment()

        fragment.arguments = Bundle().apply {
            putInt("index", tabList[position].id)
            putInt("position", position)
        }

        return fragment
    }

}