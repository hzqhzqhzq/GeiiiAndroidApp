package com.sheiii.app.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sheiii.app.GlideApp
import com.sheiii.app.R
import com.sheiii.app.model.ProductModel
import com.sheiii.app.model.SkuImage
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.view.productdetails.ProductDetailsActivity

/**
 * @author created by Zhenqing He on  15:34
 * @description
 */
class ProductListTwoAdapter(data: MutableList<MutableList<ProductModel>>, @Nullable context: Fragment?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var productView: View
    private val list = data
    private val context = context

    class ProductViewHolder(
        view: View,
        list: MutableList<MutableList<ProductModel>>,
        context: Fragment?
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var context = context
        var mList = list

        val loading = view.findViewById<ProgressBar>(R.id.product_list_loading)


        var title1: TextView = view.findViewById(R.id.title_1)
        var productImage1: ImageView = view.findViewById(R.id.recommend_prod_img_1)
        var discountTxt1: TextView = view.findViewById(R.id.discountTxt_1)
        var marketPriceTxt1: TextView = view.findViewById(R.id.marketPriceTxt_1)
        var salePriceTxt1: TextView = view.findViewById(R.id.salePriceTxt_1)
        var skuImageRecyclerView1: RecyclerView =
            view.findViewById(R.id.home_product_sku_image_recyclerview_1)
//        val bottom: LinearLayout = view.findViewById(R.id.home_product_bottom)
//        val addWish: ImageView = view.findViewById(R.id.like)


        var title2: TextView = view.findViewById(R.id.title_2)
        var productImage2: ImageView = view.findViewById(R.id.recommend_prod_img_2)
        var discountTxt2: TextView = view.findViewById(R.id.discountTxt_2)
        var marketPriceTxt2: TextView = view.findViewById(R.id.marketPriceTxt_2)
        var salePriceTxt2: TextView = view.findViewById(R.id.salePriceTxt_2)
        var skuImageRecyclerView2: RecyclerView =
            view.findViewById(R.id.home_product_sku_image_recyclerview_2)
//        val bottom: LinearLayout = view.findViewById(R.id.home_product_bottom)
//        val addWish: ImageView = view.findViewById(R.id.like)


        override fun onClick(v: View?) {
            val positon = layoutPosition

            if (context == null) {
                Log.d("pdastartcontent", "null")
                ProductDetailsActivity.actionStart(
                    MyApplication.getInstance(),
                    mList[positon][0].sid
                )
            } else {
                Log.d("pdastartcontent", "has")
                val intent = Intent(MyApplication.getInstance(), ProductDetailsActivity::class.java)
                intent.putExtra("sid", mList[positon][0].sid)
                context?.activity?.startActivityForResult(intent, 1)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        productView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_product_list_two, parent, false)
        return ProductViewHolder(productView, list, context)
    }
    //        val span = SpannableString(model.marketPriceTxt)
//        span.setSpan(
//            StrikethroughSpan(),
//            0,
//            model.marketPriceTxt.length,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ProductViewHolder
        val model1 = list[position][0]
        val model2 = list[position][1]

        holder.title1.text = model1.title
        if (model1.discountTxt != "" && model1.discountTxt != "0") {
            holder.discountTxt1.text = model1.discountTxt
        } else {
            holder.discountTxt1.visibility = View.INVISIBLE
        }

//        holder.marketPriceTxt.text = span
        holder.salePriceTxt1.text = model1.salePriceTxt
        GlideApp.with(productView).load(MyApplication.getImageHost() + model1.picUrl)
            .placeholder(R.drawable.bg_logo_jpg_405x540).into(holder.productImage1)

        holder.title1.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
//                val positon = layoutPosition
                if (context == null) {
                    Log.d("pdastartcontent", "null")
                    ProductDetailsActivity.actionStart(
                        MyApplication.getInstance(),
                        model1.sid
                    )
                } else {
                    Log.d("pdastartcontent", "has")
                    val intent = Intent(MyApplication.getInstance(), ProductDetailsActivity::class.java)
                    intent.putExtra("sid", model1.sid)
                    context?.activity?.startActivityForResult(intent, 1)
                }
            }
        })
        holder.productImage1.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
//                val positon = layoutPosition
                if (context == null) {
                    Log.d("pdastartcontent", "null")
                    ProductDetailsActivity.actionStart(
                        MyApplication.getInstance(),
                        model1.sid
                    )
                } else {
                    Log.d("pdastartcontent", "has")
                    val intent = Intent(MyApplication.getInstance(), ProductDetailsActivity::class.java)
                    intent.putExtra("sid", model1.sid)
                    context?.activity?.startActivityForResult(intent, 1)
                }
            }
        })

        // 显示产品sku，如果 image sku 数量大于 1 则显示
        if (model1.skuImageList.size > 1) {
            holder.skuImageRecyclerView1.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = CommonAdapter.Builder().setDatas(model1.skuImageList)
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
                                .load(MyApplication.getImageHost() + data.colorImage).placeholder(R.drawable.bg_logo_jpg_405x540).into(image)
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
                                        .into(holder.productImage1)
                                }
                            })
                        }
                    }).create()
            }
        } else {
        }

        holder.title2.text = model2.title
        if (model2.discountTxt != "" && model2.discountTxt != "0") {
            holder.discountTxt2.text = model2.discountTxt
        } else {
            holder.discountTxt2.visibility = View.INVISIBLE
        }

//        holder.marketPriceTxt.text = span
        holder.salePriceTxt2.text = model2.salePriceTxt
        GlideApp.with(productView).load(MyApplication.getImageHost() + model2.picUrl)
            .placeholder(R.drawable.bg_logo_jpg_405x540).into(holder.productImage2)

        holder.title2.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
//                val positon = layoutPosition
                if (context == null) {
                    Log.d("pdastartcontent", "null")
                    ProductDetailsActivity.actionStart(
                        MyApplication.getInstance(),
                        model2.sid
                    )
                } else {
                    Log.d("pdastartcontent", "has")
                    val intent = Intent(MyApplication.getInstance(), ProductDetailsActivity::class.java)
                    intent.putExtra("sid", model2.sid)
                    context?.activity?.startActivityForResult(intent, 1)
                }
            }
        })
        holder.productImage2.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
//                val positon = layoutPosition
                if (context == null) {
                    Log.d("pdastartcontent", "null")
                    ProductDetailsActivity.actionStart(
                        MyApplication.getInstance(),
                        model2.sid
                    )
                } else {
                    Log.d("pdastartcontent", "has")
                    val intent = Intent(MyApplication.getInstance(), ProductDetailsActivity::class.java)
                    intent.putExtra("sid", model2.sid)
                    context?.activity?.startActivityForResult(intent, 1)
                }
            }
        })

        // 显示产品sku，如果 image sku 数量大于 1 则显示
        if (model2.skuImageList.size > 1) {
            holder.skuImageRecyclerView2.apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = CommonAdapter.Builder().setDatas(model2.skuImageList)
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
                                .load(MyApplication.getImageHost() + data.colorImage).placeholder(R.drawable.bg_logo_jpg_405x540).into(image)

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
                                        .into(holder.productImage2)
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