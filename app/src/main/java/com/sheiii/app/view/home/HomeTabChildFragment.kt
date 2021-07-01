package com.sheiii.app.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sheiii.app.R
import com.sheiii.app.adapter.HomeModuleAdapter
import com.sheiii.app.adapter.ProductListAdapter
import com.sheiii.app.adapter.ProductListTwoAdapter
import com.sheiii.app.model.ProductModel
import com.sheiii.app.ui.multistatepage.MyLoadingState
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.ScrollUtil
import com.sheiii.app.viewmodel.HomeViewModel
import com.sheiii.app.viewmodel.LoadingViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState


/**
 * @author created by Zhenqing He on  16:14
 * @description
 */
class HomeTabChildFragment : Fragment() {
    private lateinit var root: View
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeRecommendRecyclerView: RecyclerView

    //    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var productListTwoAdapter: ProductListTwoAdapter
    private val loadingViewModel: LoadingViewModel by activityViewModels()

    /* 控制 onScrollStateChanged ，避免在页面渲染没有完成的时候多次执行 */
    private var flag = true
    private var index = 1
    private var position = 0

    private lateinit var homeModuleRecyclerView: RecyclerView

    private var recommendListTwo = mutableListOf<MutableList<ProductModel>>()

    private lateinit var multiStateContainer: MultiStateContainer

    private lateinit var refreshLayout: RefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home_tab_item, container, false)

        multiStateContainer = MultiStatePage.bindMultiState(root, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                homeViewModel.getDefaultHomePage(index)
            }
        })
//        val lottieOtherState = LottieOtherState()

        /* 数据加载及页面渲染完毕，关闭首页loading */
//        loadingViewModel.isLoading.value = View.VISIBLE

        multiStateContainer.show<MyLoadingState> { }

        refreshLayout = root.findViewById<View>(R.id.refreshLayout) as RefreshLayout
        refreshLayout.setRefreshFooter(ClassicsFooter(context))
        refreshLayout.setEnableHeaderTranslationContent(false)
        refreshLayout.setOnLoadMoreListener { refreshlayout ->
            homeViewModel.loadRecommendList(index)
//            refreshlayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
        }

        arguments?.takeIf { it.containsKey("index") }?.apply {
            index = getInt("index")
            position = getInt("position")
        }

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        homeViewModel.getDefaultHomePage(index)

//        productListAdapter = ProductListAdapter(homeViewModel.recommendList.value!!, this)

        homeRecommendRecyclerView = root.findViewById<RecyclerView>(R.id.home_product_recyclerview)
        homeModuleRecyclerView = root.findViewById(R.id.recycler_home_header)

        homeViewModel.getDefaultHomePage(index)

        return multiStateContainer

//        return loadSirServer.loadLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        /* 推荐产品滚动加载 */
        homeViewModel.recommendList.observe(viewLifecycleOwner, Observer {
            if (homeViewModel.recommendList.value!!.size > 0) {
                /* 保存滚动最后加载的数据位置 */
                val oldSize = recommendListTwo.size
                recommendListTwo.clear()
                recommendListTwo.addAll(initList(homeViewModel.recommendList.value!!))
                if (homeRecommendRecyclerView.adapter == null) {
                    productListTwoAdapter =
                        ProductListTwoAdapter(recommendListTwo, this)

                    homeRecommendRecyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager =
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        adapter = productListTwoAdapter
                    }
                    flag = true
                } else {
                    productListTwoAdapter.notifyItemRangeInserted(
                        oldSize,
                        (recommendListTwo.size - oldSize)
                    )
                    flag = true
                }
                refreshLayout.finishLoadMore()
//                HomeFragment.refreshLayout.finishRefresh()
                multiStateContainer.show<SuccessState> { }
            }
        })

        initBanner()
    }

    /**
     * 初始化banner图
     * @author Zhenqing He
     * @createDate 2021/5/21 9:55
     */
    private fun initBanner() {
        homeViewModel.moduleList.observe(viewLifecycleOwner, Observer {
            if (homeViewModel.moduleList.value!!.size > 0) {
                /* 数据加载及页面渲染完毕，关闭首页loading */
                loadingViewModel.isLoading.value = View.INVISIBLE
                val modelList = homeViewModel.moduleList.value!!

                homeModuleRecyclerView.apply {
                    setHasFixedSize(false)
                    layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter =
                        HomeModuleAdapter(modelList, viewLifecycleOwner, this@HomeTabChildFragment)
                }

                if (root.height >= homeRecommendRecyclerView.top && homeRecommendRecyclerView.height == 0) {
                    flag = false
                    homeViewModel.loadRecommendList(index)
                }
            }
        })
    }

    private fun initList(old: MutableList<ProductModel>): MutableList<MutableList<ProductModel>> {
        val result = mutableListOf<MutableList<ProductModel>>()

        if (old.size != 0) {
            for (i in 0 until old.size / 2) {
//                Log.d("all-size", old.size.toString())
//                Log.d("all-size/2", (old.size / 2).toString())
//                Log.d("i*2", (i * 2).toString())
                val temp = mutableListOf<ProductModel>()
                temp.add(old[i * 2])
                if ((i * 2 + 1) <= old.size) {
                    temp.add(old[i * 2 + 1])
                }
                result.add(temp)
            }
        }

        return result
    }


}
