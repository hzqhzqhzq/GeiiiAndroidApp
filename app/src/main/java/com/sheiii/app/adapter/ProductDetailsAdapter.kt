package com.sheiii.app.adapter

import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.iwgang.countdownview.CountdownView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sheiii.app.GlideApp
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.model.ItemProp
import com.sheiii.app.model.ProductDetailsItemBaseModel
import com.sheiii.app.model.ProductDetailsItemReviews
import com.sheiii.app.model.Reviews
import com.sheiii.app.ui.MyViewPagerr
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.view.productdetails.ProductDetailsActivity
import com.sheiii.app.view.productdetails.reviewsdetails.ReviewsDetailsActivity
import com.sheiii.app.viewmodel.ProductDetailsViewModel

/**
 * @author created by Zhenqing He on  09:39
 * @description
 */
class ProductDetailsAdapter(
    list: List<Any>,
    vm: ProductDetailsViewModel,
    context: Context,
    sid: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = list
    private val vm = vm
    private val context = context
    private val sid = sid

    private val ITEM_BASE = 0
    private val SERVICE = 1
//    private val DESC_TEXT = 3
//    private val DESC_IMAGE = 2
    private val REVIEWS = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_BASE -> {
                return ItemBaseHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_product_details_item_base, parent, false)
                )
            }
            SERVICE -> {
                return ServiceHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_product_details_service, parent, false)
                )
            }
//            DESC_TEXT -> {
//                return DescTextHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.recycler_product_details_desc_text, parent, false)
//                )
//            }
//            DESC_IMAGE -> {
//                return DescImageHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.recycler_product_details_desc_image, parent, false)
//                )
//            }
            REVIEWS -> {
                return ReviewsHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_product_details_review, parent, false)
                )
            }
//            ALSO_LIKE -> {
//                return AlsoLikeHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.recycler_product_details_also_like, parent, false)
//                )
//            }
            else -> {
                return ItemBaseHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_product_details_item_base, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            ITEM_BASE -> {
                holder as ItemBaseHolder
                val data = list[position] as ProductDetailsItemBaseModel
                /* 设置产品图片 */
                holder.imageViewPager.adapter = ProductDetailsImageAdapter(data.imageList)
                holder.imageViewPager.pageMargin = 10

//                if (data.discountTxt == "" || data.discountTxt == "0") {
                holder.discountTxt.visibility = View.GONE
//                } else {
//                    holder.discountTxt.text = data.discountTxt
//                }

                holder.productName.text = data.title
//                holder.marketPriceTxt.text = data.marketPriceTxt
                holder.marketPriceTxt.visibility = View.GONE
                holder.salePriceTxt.text = data.salePriceTxt

                /* 初始化出售数量 */
//                if (vm.soldNumber.value != null) {
                holder.soldNumber.text =
                    "${vm.soldNumber.value.toString()} ${context.resources.getString(R.string.sold_tips)}"
//                } else {
//                    holder.soldNumber.visibility = View.GONE
//                }

                /* 初始化优惠信息 */
                holder.promotion.visibility = View.VISIBLE
                var title = ""
                if (vm.promotion.value != null) {
                    for (item in vm.promotion.value!!.promoTipList) {
                        title = if (title == "") {
                            item
                        } else {
                            "${title}\n${item}"
                        }
                    }
                    holder.promotionTitle.text = title
                    holder.productCountDown.start((vm.promotion.value!!.leftSeconds.toLong() * 1000).toLong())
                } else {
                    holder.promotion.visibility = View.GONE
                }
            }
            SERVICE -> {
                holder as ServiceHolder
                val data = list[position] as List<*>
                holder.serviceRecyclerView.apply {
                    setHasFixedSize(false)
                    layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter = CommonAdapter.Builder()
                        .setDatas(data)
                        .setLayoutId(R.layout.recycler_product_details_service_item)
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                viewHolder.itemView.findViewById<TextView>(R.id.service).text =
                                    data as String
                            }
                        })
                        .create()
                }
                /* service details 点击监听 */
                holder.itemView.setOnClickListener(object :
                    PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        ProductDetailsActivity.getLoading().showLoading()
                        vm.getServiceInfo()
                    }
                })
                holder.serviceRecyclerView.setOnClickListener(object :
                    PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        ProductDetailsActivity.getLoading().showLoading()
                        vm.getServiceInfo()
                    }
                })
            }
//            DESC_TEXT -> {
//                holder as DescTextHolder
//                val data = list[position] as List<*>
//
//                holder.descTextRecyclerView.apply {
//                    setHasFixedSize(false)
//                    layoutManager =
//                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
//                    adapter = CommonAdapter.Builder().setDatas(data)
//                        .setLayoutId(R.layout.recycler_product_content_desc)
//                        .bindView(object : CommonAdapter.BindView {
//                            override fun onBindView(
//                                viewHolder: CommonAdapter.MyViewHolder,
//                                data: Any?,
//                                position: Int
//                            ) {
//                                data as ItemProp
//                                viewHolder.itemView.findViewById<TextView>(R.id.prop_value).text =
//                                    data.propValue
//                                viewHolder.itemView.findViewById<TextView>(R.id.prop_name).text =
//                                    data.propName
//                            }
//                        }).create()
//                }
////            }
//            DESC_IMAGE -> {
//                holder as DescImageHolder
//                val data = list[position] as List<*>
//
//                holder.descImageRecyclerView.apply {
//                    setHasFixedSize(false)
//                    layoutManager =
//                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                    adapter = CommonAdapter.Builder()
//                        .setDatas(data)
//                        .setLayoutId(R.layout.recycler_product_content_image)
//                        .bindView(object : CommonAdapter.BindView {
//                            override fun onBindView(
//                                viewHolder: CommonAdapter.MyViewHolder,
//                                data: Any?,
//                                position: Int
//                            ) {
//                                data as String
////                                Log.d("initItemContentImage", data)
//                                val image =
//                                    viewHolder.itemView.findViewById<ImageView>(R.id.product_content_image)
//
////                                GlideApp.with(image).load(data)
////                                    .placeholder(R.drawable.bg_logo_320x320).into(image)
//
//                            }
//                        }).create()
//                }
//                val layoutManager = holder.descImageRecyclerView.layoutManager as LinearLayoutManager
//                Log.d("lalastttt", holder.descImageRecyclerView.)
//                holder.descImageRecyclerView.addOnScrollListener(object :
//                    RecyclerView.OnScrollListener() {
//                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                        super.onScrolled(recyclerView, dx, dy)
//                        Log.d("lalast", layoutManager.findLastVisibleItemPosition().toString())
//                    }
//                })

//            }
            REVIEWS -> {
                if (list[position] != "null") {
                    holder as ReviewsHolder
                    val data = list[position] as ProductDetailsItemReviews

                    var title =
                        context.resources.getString(R.string.review) + "(" + data.reviewCount.toString() + ")"
                    holder.title.text = title
                    holder.ratingBar.rating = data.reviewRate.toFloat()

                    val reviewsAdapter = CommonAdapter.Builder()
                        .setDatas(data.list as List<*>)
                        .setLayoutId(R.layout.recycler_product_details_review_item)
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                data as Reviews
                                viewHolder.itemView.findViewById<TextView>(R.id.user_name).text =
                                    data.userName
                                viewHolder.itemView.findViewById<RatingBar>(R.id.ratingBar_child).rating =
                                    data.rate.toFloat()
                                viewHolder.itemView.findViewById<TextView>(R.id.content).text =
                                    data.content.content

                                val userIcon =
                                    viewHolder.itemView.findViewById<ImageView>(R.id.user_icon)
                                Glide.with(userIcon)
                                    .load(MyApplication.getImageHost() + data.userIcon)
                                    .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                                    .into(userIcon)

                                /* review 内的图片的 recyclerview adapter */
                                val reviewImgAdapter: CommonAdapter =
                                    CommonAdapter.Builder().setDatas(data.content.images as List<*>)
                                        .setLayoutId(R.layout.recycler_reviews_details_image)
                                        .bindView(object : CommonAdapter.BindView {
                                            override fun onBindView(
                                                viewHolder: CommonAdapter.MyViewHolder,
                                                data: Any?,
                                                position: Int
                                            ) {
                                                data as String
                                                val image =
                                                    viewHolder.itemView.findViewById<ImageView>(R.id.reviews_details_image)
                                                Glide.with(image)
                                                    .load(MyApplication.getImageHost() + data)
                                                    .into(image)
                                            }
                                        })
                                        .create()

                                //
                                viewHolder.itemView.findViewById<RecyclerView>(R.id.review_img_recycler)
                                    .apply {
                                        adapter = reviewImgAdapter
                                        layoutManager = StaggeredGridLayoutManager(
                                            4,
                                            StaggeredGridLayoutManager.VERTICAL
                                        )
                                    }
                            }
                        })
                        .create()

                    holder.reviewsRecyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager =
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        adapter = reviewsAdapter
                    }

                    /* 更多评论跳转 */
                    holder.more.setOnClickListener(object : PerfectClickListener() {
                        override fun onNoDoubleClick(v: View?) {
                            ReviewsDetailsActivity.reviewsDetailsStart(MyApplication.getInstance(), sid)
                        }
                    })
                } else {
                    holder.itemView.visibility = View.GONE
                    val layoutParams = holder.itemView.layoutParams
                    layoutParams.height = 0
                    holder.itemView.layoutParams = layoutParams
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ITEM_BASE
            }
            1 -> {
                SERVICE
            }
            2 -> {
                REVIEWS
            }
//            3 -> {
//                DESC_TEXT
//            }
//            4 -> {
//                REVIEWS
//            }
            else -> {
                -1
            }
        }
    }

    class ItemBaseHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName = view.findViewById<TextView>(R.id.title)
        val discountTxt = view.findViewById<TextView>(R.id.discountTxt)
        val salePriceTxt = view.findViewById<TextView>(R.id.salePriceTxt)
        val marketPriceTxt = view.findViewById<TextView>(R.id.marketPriceTxt)
        val soldNumber = view.findViewById<TextView>(R.id.sold_number_txt)
        val imageViewPager = view.findViewById<MyViewPagerr>(R.id.product_details_viewpager)
        val promotion = view.findViewById<ConstraintLayout>(R.id.product_details_flash)
        val productCountDown = view.findViewById<CountdownView>(R.id.flashdeal_countdown)
        val promotionTitle = view.findViewById<TextView>(R.id.promotion_title)
    }

    class ServiceHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceRecyclerView = view.findViewById<RecyclerView>(R.id.service_list)
    }

    class DescTextHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descTextRecyclerView =
            view.findViewById<RecyclerView>(R.id.description_content_recyclerview)
    }

    class DescImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descImageRecyclerView =
            view.findViewById<RecyclerView>(R.id.description_image_recyclerview).apply {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        Log.d("onScrollStateChanged", newState.toString())
                    }
                })
            }
    }

    class ReviewsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.reviews_title)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val more = view.findViewById<ConstraintLayout>(R.id.more_reviews)
        val reviewsRecyclerView = view.findViewById<RecyclerView>(R.id.review_recycler)
    }

    class AlsoLikeHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}