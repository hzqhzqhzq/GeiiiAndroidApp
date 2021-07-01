package com.sheiii.app.view.order

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.databinding.ActivityOrderDetailsBinding
import com.sheiii.app.model.OrderDetails
import com.sheiii.app.model.OrderItem
import com.sheiii.app.util.MyApplication
import com.sheiii.app.viewmodel.OrderViewModel

class OrderDetailsActivity : AppCompatActivity() {
    private var orderId = ""
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var orderDetailsRecyclerView: RecyclerView
    private lateinit var binding: ActivityOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details)

        orderId = intent.getStringExtra("id").toString()
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        orderViewModel.getOrderDetails(orderId)

        orderDetailsRecyclerView = findViewById(R.id.order_details_recyclerview)
    }

    override fun onResume() {
        super.onResume()
        orderViewModel.orderDetails.observe(this, Observer {
            if (orderViewModel.orderDetails.value != null) {
                initOrderDetails(orderViewModel.orderDetails.value!!)
            }
        })
    }

    private fun initOrderDetails(data: OrderDetails) {
        // databinding 数据绑定
        binding.setVariable(BR.order_details, data)
        // 支付方式图片
        val paymentImage = findViewById<ImageView>(R.id.order_details_payment_image)
        var paymentImageUrl = ""
        for (payment in data.paymentList) {
            if (payment.paymentId == data.paymentId) {
                paymentImageUrl = payment.iconUrl
                break
            }
        }
        Glide.with(paymentImage).load(MyApplication.getImageHost() + paymentImageUrl)
            .into(paymentImage)

        // 订单产品recyclerview初始化
        orderDetailsRecyclerView.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = CommonAdapter.Builder().setDatas(data.itemList)
                .setLayoutId(R.layout.recycler_order_details_product_item)
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

                        Glide.with(image).load(MyApplication.getImageHost() + data.image)
                            .into(image)
                    }
                }).create()
        }
    }


    companion object {
        fun actionStart(context: Context, id: String) {
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }
    }
}