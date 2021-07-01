package com.sheiii.app.view.home

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.sheiii.app.R
import com.sheiii.app.adapter.HomeTabFragmentAdapter
import com.sheiii.app.viewmodel.HomeViewModel
import com.sheiii.app.viewmodel.LoadingViewModel


class HomeFragment : Fragment() {

    companion object {
        lateinit var refreshLayout: SmartRefreshLayout
    }

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeTabFragmentAdapter: HomeTabFragmentAdapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val loadingViewModel: LoadingViewModel by activityViewModels()

//    private lateinit var loading: ViewStub
    private lateinit var savedState: Bundle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager2 = root.findViewById(R.id.home_viewpager)

//        refreshLayout = root.findViewById<SmartRefreshLayout>(R.id.refreshLayout)
//        refreshLayout.setEnableLoadMore(false)
//        refreshLayout.setEnableHeaderTranslationContent(false)
//        refreshLayout.setOnRefreshListener {
//            viewPager2.adapter = homeTabFragmentAdapter
//            refreshLayout.finishRefresh()
//        }

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeTabFragmentAdapter = HomeTabFragmentAdapter(this, homeViewModel.tabList.value!!)
        viewPager2.adapter = homeTabFragmentAdapter

        homeViewModel.getDefaultHomePage(1)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /* 控制首页初始化的loading */
//        loading = view.findViewById(R.id.loading)
//        loading.visibility = View.VISIBLE

        tabLayout = view.findViewById(R.id.home_tab)

        homeViewModel.tabList.observe(viewLifecycleOwner, Observer {
            if (homeViewModel.moduleList.value?.size!! > 0) {
//                for (tab in homeViewModel.tabList.value!!) {
//                    tabLayout.addTab(tabLayout.newTab().setText(tab.tabName))
//                }
//                homeTabFragmentAdapter.notifyDataSetChanged()
                homeTabFragmentAdapter = HomeTabFragmentAdapter(this, homeViewModel.tabList.value!!)
                viewPager2.adapter = homeTabFragmentAdapter

                viewPager2.isUserInputEnabled = false

                TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                    tab.text = homeViewModel.tabList.value?.get(position)?.tabName
                    tab.tag = homeViewModel.tabList.value?.get(position)?.id
                }.attach()
                loadingViewModel.isLoading.value = View.INVISIBLE
            }
        })

//        loadingViewModel.isLoading.observe(viewLifecycleOwner, Observer {
//            loading.visibility = loadingViewModel.isLoading.value!!
//        })
    }

//    override fun onAttachFragment(childFragment: Fragment) {
//        super.onAttachFragment(childFragment)
//    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        if (!restoreStateFromArguments()) {
//
//        }
//    }
//
//    private fun onFirstTimeLaunched() {
//
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        saveStateToArguments()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        saveStateToArguments()
//    }
//
//    private fun saveStateToArguments() {
//        if (view != null) {
//            savedState = saveState()
//        }
//        if (savedState != null) {
//            arguments?.putBundle("11111", savedState)
//        }
//    }
//
//    private fun restoreStateFromArguments() : Boolean {
//        savedState = arguments?.getBundle("11111")!!
//        if (savedState != null) {
//            restoreState()
//            return true
//        }
//        return false
//    }
//
//    private fun restoreState() {
//        if (savedState != null) {
//            onRestoreState(savedState)
//        }
//    }
//
//    private fun onRestoreState(savedInstanceState: Bundle) {
//
//    }
//
//    private fun saveState() : Bundle {
//        val state = Bundle()
//        onSaveState(state)
//        return state
//    }
//
//    private fun onSaveState(outState: Bundle) {
//
//    }
}