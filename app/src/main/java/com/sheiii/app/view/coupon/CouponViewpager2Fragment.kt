package com.sheiii.app.view.coupon

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.constants.COUPON_EXPIRED
import com.sheiii.app.constants.COUPON_UNUSED
import com.sheiii.app.constants.COUPON_USED
import com.sheiii.app.model.Coupon
import com.sheiii.app.ui.CommonLoadingDialog
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.CouponViewModel

/**
 * @author created by Zhenqing He on  14:02
 * @description
 */
class CouponViewpager2Fragment(position: Int, couponList: List<Coupon>?) : Fragment() {
    private val position = position
    private var couponStatus = 0

    //    private val couponList = couponList
    private val couponViewModel: CouponViewModel by activityViewModels()
    private lateinit var root: View
    private lateinit var couponRecyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout

    private lateinit var loading: CommonLoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_coupon_viewpager, container, false)
        couponRecyclerView = root.findViewById(R.id.coupon_recyclerView)
        emptyView = root.findViewById(R.id.coupon_empty_layout)

        loading = context?.let { MyApplication.getLoadingByActivity(it) }!!

        when (position) {
            0 -> {
                couponStatus = COUPON_UNUSED
                couponViewModel.getCouponPage(COUPON_UNUSED)
            }
            1 -> {
                couponStatus = COUPON_EXPIRED
                couponViewModel.getCouponPage(COUPON_EXPIRED)
            }
            2 -> {
                couponStatus = COUPON_USED
                couponViewModel.getCouponPage(COUPON_USED)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        loading.showLoading()

        couponViewModel.couponPager.observe(viewLifecycleOwner, Observer {
            val data = couponViewModel.couponPager.value
            if (data != null) {
                initCoupons()
            }
        })
    }

    /**
     * null
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/23 15:31
     */
    private fun initCoupons() {
        val couponList: List<Coupon>? = couponViewModel.couponMap.value?.get(couponStatus)

        if (couponList == null || couponList.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            couponRecyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            couponRecyclerView.visibility = View.VISIBLE
            couponRecyclerView.apply {
                setHasFixedSize(false)
                layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                adapter = CommonAdapter.Builder().setDatas(couponList)
                    .setLayoutId(if (position == 0) R.layout.recycler_coupon_unused else R.layout.recycler_coupon_expired)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            data as Coupon
                            val view = viewHolder.itemView
                            view.findViewById<TextView>(R.id.coupon_price)
                                ?.let { it.text = data.couponPriceText }
                            view.findViewById<TextView>(R.id.coupon_disable_name).text =
                                data.couponName
                            view.findViewById<TextView>(R.id.coupon_disable_desc).text =
                                data.couponDesc
                            view.findViewById<TextView>(R.id.coupon_end_time).text =
                                "${resources.getString(R.string.expired_on)}: ${data.endTime}"

                            view.findViewById<TextView>(R.id.coupon_status)?.let {
                                when (couponStatus) {
                                    COUPON_EXPIRED -> {
                                        it.text = resources.getString(R.string.expired)
                                    }
                                    COUPON_USED -> {
                                        it.text = resources.getString(R.string.used)
                                    }
                                    else -> {
                                        it.setOnClickListener(object : PerfectClickListener() {
                                            override fun onNoDoubleClick(v: View?) {
                                                val intent = Intent()
                                                activity?.setResult(RESULT_OK, intent)
                                                activity?.finish()
                                            }
                                        })
                                    }
                                }
                            }
                        }
                    }).create()
            }
        }

        loading.hideLoading()
    }
}