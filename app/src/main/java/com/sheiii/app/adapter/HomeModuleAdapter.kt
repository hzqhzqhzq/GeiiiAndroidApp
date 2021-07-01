package com.sheiii.app.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.iwgang.countdownview.CountdownView
import com.bumptech.glide.request.target.SimpleTarget
import com.sheiii.app.GlideApp
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.model.HomeModelList
import com.sheiii.app.model.HomeModuleProduct
import com.sheiii.app.model.Image
import com.sheiii.app.ui.Banner
import com.sheiii.app.util.GetUrlParamsUtil
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.view.category.CategoryActivity
import com.sheiii.app.view.productdetails.ProductDetailsActivity
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

/**
 * @author created by Zhenqing He on  15:02
 * @description
 */
class HomeModuleAdapter(
    list: List<HomeModelList>,
    viewLifecycleOwner: LifecycleOwner,
    context: Fragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = list
    private val viewLifecycleOwner = viewLifecycleOwner
    private val context = context

    private val BANNER_TYPE = 1
    private val ICON_TYPE = 2
    private val FLASHDEAL_TYPE = 3
    private val COUPON_TYPE = 4
    private val HOT_TYPE = 5
    private val RECOMMEND_TYPE = 6

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BANNER_TYPE -> {
                return HomeBannerHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_home_banner_item, parent, false)
                )
            }
            ICON_TYPE -> {
                return HomeIconHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_home_icon, parent, false)
                )
            }
            FLASHDEAL_TYPE -> {
                return HomeFlashDealHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_home_flashdeal, parent, false)
                )
            }
            COUPON_TYPE -> {
                return HomeCouponImageHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_home_coupon, parent, false)
                )
            }
            HOT_TYPE -> {
                return HomeNewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_home_hot_new_recommend, parent, false)
                )
            }
            RECOMMEND_TYPE -> {
                return HomeNewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_home_hot_new_recommend, parent, false)
                )
            }
            else -> {
                return HomeBannerHolder(View(MyApplication.getInstance()))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (list[position].typeId) {
            BANNER_TYPE -> {
                holder as HomeBannerHolder
                val data = list[position]
                val banner = holder.banner

                getBannerHeight(
                    MyApplication.getImageHost() + data.data.imageList[0].imgUrl,
                    banner
                )
                banner.setAdapter(object : BannerImageAdapter<Image>(data.data.imageList) {
                    override fun onBindView(
                        holder: BannerImageHolder?,
                        data: Image?,
                        position: Int,
                        size: Int
                    ) {
                        if (holder != null && data != null) {
                            GlideApp.with(holder.imageView)
                                .load(MyApplication.getImageHost() + data.imgUrl)
                                //                                            .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                                .into(holder.imageView)
                            banner.invalidate()
                            holder.imageView.setOnClickListener(object : PerfectClickListener() {
                                override fun onNoDoubleClick(v: View?) {
                                    CategoryActivity.actionStart(
                                        context.requireContext(),
                                        data.url,
                                        true
                                    )
                                }
                            })
                        }
                    }
                }).addBannerLifecycleObserver(viewLifecycleOwner).indicator =
                    CircleIndicator(MyApplication.getInstance())
                banner.isAutoLoop(false)
            }
            ICON_TYPE -> {
                holder as HomeIconHolder
                val data = list[position]

                holder.homeIconRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = StaggeredGridLayoutManager(
                        4,
                        StaggeredGridLayoutManager.VERTICAL
                    )
                    adapter = CommonAdapter.Builder().setDatas(data.data.imageList)
                        .setLayoutId(R.layout.recycler_home_icon_item)
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                data as Image

                                val homeIconName =
                                    viewHolder.itemView.findViewById<TextView>(R.id.home_icon_name)
                                val homeIconImg =
                                    viewHolder.itemView.findViewById<ImageView>(R.id.home_icon_img)

                                homeIconName.text = data.title
                                GlideApp.with(homeIconImg)
                                    .load(MyApplication.getImageHost() + data.imgUrl)
                                    .into(homeIconImg)

                                viewHolder.itemView.setOnClickListener(object :
                                    PerfectClickListener() {
                                    override fun onNoDoubleClick(v: View?) {
                                        CategoryActivity.actionStart(context, data.url, true)
                                    }
                                })
                            }

                        }).create()
                }
            }
            FLASHDEAL_TYPE -> {
                holder as HomeFlashDealHolder
                val data = list[position]
                if (data.data.leftSeconds != 0) {
                    // 启动倒计时
                    holder.countdownView.start((data.data.leftSeconds.toLong() * 1000).toLong())
//                holder.countdownView.start(((33*24*60).toLong()*60*1000).toLong())

                    if (data.data.itemList != null) {
                        holder.productRecyclerView.apply {
                            setHasFixedSize(true)
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = CommonAdapter.Builder().setDatas(data.data.itemList)
                                .setLayoutId(R.layout.recycler_home_header_product)
                                .addFootView(
                                    LayoutInflater.from(context)
                                        .inflate(
                                            R.layout.recycler_home_header_product_footer,
                                            this,
                                            false
                                        )
                                )
                                .bindView(object : CommonAdapter.BindView {
                                    override fun onBindView(
                                        viewHolder: CommonAdapter.MyViewHolder,
                                        data: Any?,
                                        position: Int
                                    ) {
                                        data as HomeModuleProduct

                                        viewHolder.itemView.setOnClickListener(object :
                                            PerfectClickListener() {
                                            override fun onNoDoubleClick(v: View?) {
                                                //                                            ProductDetailsActivity.actionStart(context, data.sid)
                                                val intent = Intent(
                                                    MyApplication.getInstance(),
                                                    ProductDetailsActivity::class.java
                                                )
                                                intent.putExtra("sid", data.sid)
                                                (context as MainActivity).startActivityForResult(
                                                    intent,
                                                    1
                                                )
                                            }
                                        })

                                        val image =
                                            viewHolder.itemView.findViewById<ImageView>(R.id.home_header_product_image)
                                        val saleprice =
                                            viewHolder.itemView.findViewById<TextView>(R.id.home_header_product_saleprice)
                                        val marketprice =
                                            viewHolder.itemView.findViewById<TextView>(R.id.home_header_product_marketprice)

                                        saleprice.text = data.salePriceTxt
                                        marketprice.text = data.marketPriceTxt
                                        GlideApp.with(image)
                                            .load(MyApplication.getImageHost() + data.picUrl)
                                            .into(image)
                                    }
                                }).create()
                        }
                    }

                } else {
                    holder.itemView.visibility = View.GONE
                }
            }
            COUPON_TYPE -> {
                holder as HomeCouponImageHolder
                val data = list[position]
                holder.recyclerView.apply {
                    setHasFixedSize(false)
                    layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter = CommonAdapter.Builder().setDatas(data.data.imageList)
                        .setLayoutId(R.layout.recycler_home_coupon_image)
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                data as Image
                                val image =
                                    viewHolder.itemView.findViewById<ImageView>(R.id.coupon_image)
                                GlideApp.with(image)
                                    .load(MyApplication.getImageHost() + data.imgUrl).into(image)
                            }
                        }).create()
                }
            }
            5 -> {
                holder as HomeNewHolder
                val data = list[position]

                GlideApp.with(holder.bannerImage)
                    .load(MyApplication.getImageHost() + data.data.banner).into(holder.bannerImage)

                holder.bannerImage.setOnClickListener(object :
                    PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        CategoryActivity.actionStart(context.requireContext(), data.data.goUrl, true)
                    }
                })

                holder.productRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = CommonAdapter.Builder().setDatas(data.data.itemList)
                        .setLayoutId(R.layout.recycler_home_header_product)
                        .addFootView(
                            LayoutInflater.from(context)
                                .inflate(R.layout.recycler_home_header_product_footer, this, false)
                        )
                        .setFootViewClickListener(View.OnClickListener {
                            CategoryActivity.actionStart(
                                context,
                                data.data.goUrl,
                                true
                            )
                        })
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                data as HomeModuleProduct

                                viewHolder.itemView.setOnClickListener(object :
                                    PerfectClickListener() {
                                    override fun onNoDoubleClick(v: View?) {
                                        val intent = Intent(
                                            MyApplication.getInstance(),
                                            ProductDetailsActivity::class.java
                                        )
                                        intent.putExtra("sid", data.sid)
                                        (context as MainActivity).startActivityForResult(intent, 1)
                                    }
                                })

                                val image =
                                    viewHolder.itemView.findViewById<ImageView>(R.id.home_header_product_image)
                                val saleprice =
                                    viewHolder.itemView.findViewById<TextView>(R.id.home_header_product_saleprice)
                                val marketprice =
                                    viewHolder.itemView.findViewById<TextView>(R.id.home_header_product_marketprice)

                                saleprice.text = data.salePriceTxt
                                marketprice.text = data.marketPriceTxt
                                GlideApp.with(image)
                                    .load(MyApplication.getImageHost() + data.picUrl).into(image)
                            }
                        }).create()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].typeId) {
            1 -> {
                BANNER_TYPE
            }
            2 -> {
                ICON_TYPE
            }
            3 -> {
                FLASHDEAL_TYPE
            }
            4 -> {
                COUPON_TYPE
            }
            5 -> {
                HOT_TYPE
            }
            6 -> {
                RECOMMEND_TYPE
            }
            else -> 0
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class HomeBannerHolder(view: View) : RecyclerView.ViewHolder(view) {
        val banner = view.findViewById<Banner<Image, BannerImageAdapter<Image>>>(R.id.home_banner)
    }

    class HomeIconHolder(view: View) : RecyclerView.ViewHolder(view) {
        val homeIconRecyclerView = view.findViewById<RecyclerView>(R.id.recycler_home_icon)
    }

    class HomeFlashDealHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productRecyclerView =
            view.findViewById<RecyclerView>(R.id.home_header_product_recyclerview)
        val more = view.findViewById<TextView>(R.id.home_header_flashdeal_more)
        val countdownView = view.findViewById<CountdownView>(R.id.home_flashdeal_countdown)
    }

    class HomeCouponImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        val image = view.findViewById<ImageView>(R.id.coupon_image)
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_coupon_image_recyclerview)
    }

    class HomeNewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bannerImage = view.findViewById<ImageView>(R.id.home_header_banner_image)
        val productRecyclerView =
            view.findViewById<RecyclerView>(R.id.home_header_product_recyclerview)
    }

//    class HomeHotHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val bannerImage = view.findViewById<ImageView>(R.id.home_header_banner_image)
//        val productRecyclerView =
//            view.findViewById<RecyclerView>(R.id.home_header_product_recyclerview)
//    }
//
//    class HomeRecommendHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val bannerImage = view.findViewById<ImageView>(R.id.home_header_banner_image)
//        val productRecyclerView =
//            view.findViewById<RecyclerView>(R.id.home_header_product_recyclerview)
//    }

    /**
     * 计算banner图片的高度之后再设置
     *
     * @param url
     */
    private fun getBannerHeight(url: String, banner: Banner<Image, BannerImageAdapter<Image>>) {
        var realWidth = 0
        var realHeight = 0

        GlideApp.with(context)
            .asBitmap()
            .load(url)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {

                    realWidth = resource.width
                    realHeight = resource.height


                    banner.setViewPager2Height(((Resources.getSystem().displayMetrics.widthPixels * 1.0 / realWidth * 1.0) * realHeight).toInt())
                }
            })
    }
}