package com.sheiii.app.adapter

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sheiii.app.GlideApp
import com.sheiii.app.R
import com.sheiii.app.model.*
import com.sheiii.app.view.productdetails.ProductDetailsActivity
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener

class ProductListAdapter(list: MutableList<ProductModel>, @Nullable context: Fragment?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var productView: View
    private val list = list
    private val context = context

    class ProductViewHolder(
        view: View,
        list: MutableList<ProductModel>,
        context: Fragment?
    ) : RecyclerView.ViewHolder(view) {
        var context = context
        var thisView = view
        var title: TextView = view.findViewById(R.id.title)
        var productImage: ImageView = view.findViewById(R.id.recommend_prod_img)
        var discountTxt: TextView = view.findViewById(R.id.discountTxt)
        var marketPriceTxt: TextView = view.findViewById(R.id.marketPriceTxt)
        var salePriceTxt: TextView = view.findViewById(R.id.salePriceTxt)

        var skuImageRecyclerView: RecyclerView =
            view.findViewById(R.id.home_product_sku_image_recyclerview)
//        val bottom: LinearLayout = view.findViewById(R.id.home_product_bottom)

        var mList = list

//        val addWish: ImageView = view.findViewById(R.id.like)

//        override fun onNoDoubleClick(v: View?) {
//            val positon = layoutPosition
//
//            if (context == null) {
//                Log.d("pdastartcontent", "null")
//                ProductDetailsActivity.actionStart(
//                    MyApplication.getInstance(),
//                    mList[positon].sid
//                )
//            } else {
//                Log.d("pdastartcontent", "has")
//                val intent = Intent(MyApplication.getInstance(), ProductDetailsActivity::class.java)
//                intent.putExtra("sid", mList[positon].sid)
//                context?.activity?.startActivityForResult(intent, 1)
//            }
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        productView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_product, parent, false)
        return ProductViewHolder(productView, list, context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ProductViewHolder
        val model = list[position]
//        val span = SpannableString(model.marketPriceTxt)
//        span.setSpan(
//            StrikethroughSpan(),
//            0,
//            model.marketPriceTxt.length,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
        holder.title.text = model.title

        if (model.discountTxt != "" && model.discountTxt != "0") {
            holder.discountTxt.text = model.discountTxt
        } else {
            holder.discountTxt.visibility = View.INVISIBLE
        }

//        holder.marketPriceTxt.text = span
        holder.salePriceTxt.text = model.salePriceTxt
        GlideApp.with(productView).load(MyApplication.getImageHost() + model.picUrl)
            .placeholder(R.drawable.bg_logo_jpg_405x540).into(holder.productImage)

        holder.title.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
//                val positon = layoutPosition
                if (context == null) {
                    Log.d("pdastartcontent", "null")
                    ProductDetailsActivity.actionStart(
                        MyApplication.getInstance(),
                        model.sid
                    )
                } else {
                    Log.d("pdastartcontent", "has")
                    val intent = Intent(MyApplication.getInstance(), ProductDetailsActivity::class.java)
                    intent.putExtra("sid", model.sid)
                    context?.activity?.startActivityForResult(intent, 1)
                }
            }
        })
        holder.productImage.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
//                val positon = layoutPosition
                if (context == null) {
                    Log.d("pdastartcontent", "null")
                    ProductDetailsActivity.actionStart(
                        MyApplication.getInstance(),
                        model.sid
                    )
                } else {
                    Log.d("pdastartcontent", "has")
                    val intent = Intent(MyApplication.getInstance(), ProductDetailsActivity::class.java)
                    intent.putExtra("sid", model.sid)
                    context?.activity?.startActivityForResult(intent, 1)
                }
            }
        })

        // 显示产品sku，如果 image sku 数量大于 1 则显示
        if (model.skuImageList.size > 1) {
            holder.skuImageRecyclerView.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = CommonAdapter.Builder().setDatas(model.skuImageList)
                    .setLayoutId(R.layout.recycler_home_product_sku_image)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            data as SkuImage
                            val image =
                                viewHolder.itemView.findViewById<ImageView>(R.id.home_product_sku_image)

                            // 清除缓存，防止点击的时候，主图图片模糊
                            GlideApp.with(image)
                                .load(MyApplication.getImageHost() + data.colorImage)
                                .placeholder(R.drawable.bg_logo_jpg_405x540).into(image)

                            // 点击sku图片，更换主图
                            image.setOnClickListener(object : PerfectClickListener() {
                                override fun onNoDoubleClick(v: View?) {
                                    GlideApp.with(productView)
                                        .load(
                                            MyApplication.getImageHost() + data.colorImage.replace(
                                                "_80x80",
                                                "_405x540"
                                            )
                                        )
                                        .placeholder(R.drawable.bg_logo_jpg_405x540)
                                        .into(holder.productImage)
                                }
                            })
                        }
                    }).create()
            }
        } else {
        }
    }

    override fun getItemCount(): Int {
        return if (list.isNotEmpty()) {
            list.size
        } else {
            0
        }
    }

}