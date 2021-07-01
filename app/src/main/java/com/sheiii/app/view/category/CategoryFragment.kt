package com.sheiii.app.view.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sheiii.app.R
import com.sheiii.app.adapter.CategoryTabAdapter
import com.sheiii.app.viewmodel.CategoryViewModel
import com.sheiii.app.ui.MyVerticalTabLayout
import com.sheiii.app.ui.NoScrollViewPager
import com.sheiii.app.ui.multistatepage.MyLoadingState
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState

class CategoryFragment : Fragment() {
    private lateinit var root: View
    private val myContext = context
    private lateinit var myLifecycleOwner: LifecycleOwner
    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var tabLayout: MyVerticalTabLayout
    private lateinit var viewPager2: NoScrollViewPager

    private lateinit var multiStateContainer: MultiStateContainer

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_category, container, false)
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        multiStateContainer = MultiStatePage.bindMultiState(root, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                categoryViewModel.getTopList()
            }
        })

        multiStateContainer.show<MyLoadingState> {  }

        tabLayout = root.findViewById(R.id.category_tablayout)
        viewPager2 = root.findViewById(R.id.category_viewpager)

        categoryViewModel.getTopList()

        myLifecycleOwner = viewLifecycleOwner

        return multiStateContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTopList()
    }

    /**
     * 初始化 分类 top list
     */
    private fun initTopList() {
        categoryViewModel.topList.observe(myLifecycleOwner, Observer {
            if (categoryViewModel.topList.value != null) {
                val fragmentList: MutableList<CategoryDetailsFragment> = mutableListOf()
                for ((position, tab) in categoryViewModel.topList.value!!.withIndex()) {
                    fragmentList.add(CategoryDetailsFragment(position, tab.id))
                }


                viewPager2.adapter = CategoryTabAdapter(parentFragmentManager, categoryViewModel.topList.value!!, fragmentList)

//                viewPager2.isUserInputEnabled = false

                tabLayout.setupWithViewPager(viewPager2)

                multiStateContainer.show<SuccessState> {  }

//                TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
//                    tab.text = categoryViewModel.topList.value!![position].title
//                    tab.tag = categoryViewModel.tabList.value?.get(position)?.id
//                }.attach()
            }
        })
    }
}