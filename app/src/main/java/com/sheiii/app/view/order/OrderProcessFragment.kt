package com.sheiii.app.view.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableMap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.BezierRadarHeader
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sheiii.app.R
import com.sheiii.app.adapter.OrderProductAdapter
import com.sheiii.app.model.Order
import com.sheiii.app.model.OrderList
import com.sheiii.app.ui.multistatepage.MyLoadingState
import com.sheiii.app.viewmodel.OrderViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.EmptyState
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState

class OrderProcessFragment(orderState: String) : Fragment() {
    private val orderViewModel: OrderViewModel by activityViewModels()
    private lateinit var root: View
    private lateinit var orderRecyclerView: RecyclerView
    private val orderState = orderState

    private lateinit var multiStateContainer: MultiStateContainer
    private lateinit var refreshLayout: RefreshLayout

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_order_process, container, false)

        multiStateContainer = MultiStatePage.bindMultiState(root, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                orderViewModel.getOrderList(orderState)
            }
        })
        multiStateContainer.show<MyLoadingState> { }

        refreshLayout = root.findViewById<View>(R.id.refreshLayout) as RefreshLayout
        refreshLayout.setRefreshHeader(MaterialHeader(context))
        refreshLayout.setRefreshFooter(ClassicsFooter(context))
        refreshLayout.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout) {
                multiStateContainer.show<LoadingState> { }
                orderRecyclerView.adapter = null
                orderViewModel.getOrderList(orderState)
            }
        })
        refreshLayout.setOnLoadMoreListener { refreshlayout ->
            orderViewModel.getOrderList(orderState)
        }

        orderViewModel.getOrderList(orderState)

        orderRecyclerView = root.findViewById(R.id.order_process_recyclerview)

        initOrderList()

        return multiStateContainer
    }


    private fun initOrderList() {
        orderViewModel.orderList.observe(viewLifecycleOwner, Observer {
//            Log.d("initOrderList1", orderState)
//            Log.d("initOrderList2", orderViewModel.orderTabsDataMap.value?.get(orderState).toString())
            if (orderViewModel.orderTabsDataMap.value?.get(orderState) != null) {
                val data = orderViewModel.orderTabsDataMap.value?.get(orderState)
                if (orderRecyclerView.adapter == null) {
                    orderRecyclerView.apply {
                        setHasFixedSize(false)
                        layoutManager =
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        adapter = OrderProductAdapter(
                            data?.list!!,
                            context
                        )
                    }
                    if (orderViewModel.orderTabsDataMap.value?.get(orderState)?.list!!.isEmpty()) {
                        multiStateContainer.show<EmptyState> { }
                    } else {
                        multiStateContainer.show<SuccessState> { }
                    }
                } else {
                    orderRecyclerView.adapter?.notifyItemRangeChanged((data?.list?.size?.minus(data.pageSize)!!), data.pageSize)
                }

                refreshLayout.finishRefresh()
                refreshLayout.finishLoadMore()
            }
        })
    }

}