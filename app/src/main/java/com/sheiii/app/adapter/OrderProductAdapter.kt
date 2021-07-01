package com.sheiii.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.sheiii.app.R
import com.sheiii.app.model.Order
import com.sheiii.app.model.OrderItem
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.view.order.OrderDetailsActivity
import com.sheiii.app.view.order.OrderTrackingActivity

/**
 * @author created by Zhenqing He on  10:16
 * @description
 */
class OrderProductAdapter(dataList: List<Order>, context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = dataList
    private val mContext = context

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderNum = view.findViewById<TextView>(R.id.process_order_num)
        val orderState = view.findViewById<TextView>(R.id.process_order_state)
        val orderTotalPrice = view.findViewById<TextView>(R.id.process_total_price)
        val orderDetails = view.findViewById<TextView>(R.id.process_order_details)
        val orderTraking = view.findViewById<TextView>(R.id.process_order_traking)
        val orderProductRecyclerView =
            view.findViewById<RecyclerView>(R.id.order_process_product_recyclerview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_order_process_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as OrderViewHolder
        val data = list[position]

        holder.orderNum.text =
            "${MyApplication.getInstance().resources.getString(R.string.order)} # ${data.orderNo}"
        holder.orderState.text = data.orderStatusTxt
        holder.orderTotalPrice.text =
            "${MyApplication.getInstance().resources.getString(R.string.total)}：${data.totalPayPriceTxt}"

        // 订单产品列表
        holder.orderProductRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = CommonAdapter.Builder().setDatas(data.itemList as List<*>)
                .setLayoutId(R.layout.recycler_order_process_product_item)
                .bindView(object : CommonAdapter.BindView {
                    override fun onBindView(
                        viewHolder: CommonAdapter.MyViewHolder,
                        data: Any?,
                        position: Int
                    ) {
                        data as OrderItem

                        val image =
                            viewHolder.itemView.findViewById<ImageView>(R.id.order_details_product_image)
                        viewHolder.itemView.findViewById<TextView>(R.id.order_details_product_name).text =
                            data.itemName
                        viewHolder.itemView.findViewById<TextView>(R.id.order_details_product_sku).text =
                            data.skuName
                        viewHolder.itemView.findViewById<TextView>(R.id.order_details_product_price).text =
                            data.salePriceTxt
                        viewHolder.itemView.findViewById<TextView>(R.id.order_details_product_buynum).text =
                            "x ${data.buyNum}"

                        Glide.with(image).load(MyApplication.getImageHost() + data.image).placeholder(R.drawable.bg_logo_jpg_405x540)
                            .into(image)
                    }

                }).create()
        }
        // 订单追踪点击监听
//        holder.orderTraking.setOnClickListener {
//            OrderTrackingActivity.actionStart(mContext)
//        }
        holder.orderTraking.visibility = View.GONE
        // 订单详情点击监听
        holder.orderDetails.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                OrderDetailsActivity.actionStart(mContext, data.orderId)
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}