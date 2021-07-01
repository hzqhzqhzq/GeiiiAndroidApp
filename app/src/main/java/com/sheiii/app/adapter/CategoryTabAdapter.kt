package com.sheiii.app.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sheiii.app.model.CategoryTop
import com.sheiii.app.view.category.CategoryDetailsFragment

/**
 * @author created by Zhenqing He on  18:06
 * @description
 */
//class CategoryTabAdapter(fa: Fragment, tabList: MutableList<CategoryTop>) : FragmentStateAdapter(fa){
//    private val tabList = tabList
//
//    override fun getItemCount(): Int = tabList.size
//
//    override fun createFragment(position: Int): Fragment {
//        val fragment = CategoryDetailsFragment()
//
//        fragment.arguments = Bundle().apply {
//            putInt("index", tabList[position].id)
//            putInt("position", position)
//        }
//
//        return fragment
//    }
//
//}

class CategoryTabAdapter(
    fa: FragmentManager,
    tabList: MutableList<CategoryTop>,
    fragmentList: MutableList<CategoryDetailsFragment>
) : FragmentStatePagerAdapter(fa, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val tabList = tabList
    private val fragmentList = fragmentList

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabList[position].title
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

}