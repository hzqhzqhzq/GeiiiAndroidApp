package com.sheiii.app.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sheiii.app.model.AreaListModel
import com.sheiii.app.view.address.AreaFragment

/**
 * @author created by Zhenqing He on  15:39
 * @description 地址选择器 viewpager2 adapter
 */
    class AreaFragmentAdapter(fa: FragmentManager, life : Lifecycle, data: MutableList<AreaListModel>) : FragmentStateAdapter(fa, life) {
    private val data = data
    private lateinit var fragment: AreaFragment

    override fun getItemCount(): Int = data.size

    override fun createFragment(position: Int): Fragment {
        fragment = AreaFragment(data[position], position)
        fragment.arguments = Bundle().apply {
            putInt("index", position)
        }
        return fragment
    }

    fun update() {
        fragment.update()
    }
}