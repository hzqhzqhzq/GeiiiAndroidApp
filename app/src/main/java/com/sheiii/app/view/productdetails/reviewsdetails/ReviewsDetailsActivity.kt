package com.sheiii.app.view.productdetails.reviewsdetails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.Review
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.ReviewsDetailsViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState

class ReviewsDetailsActivity : AppCompatActivity() {
    private lateinit var reviewsDetailsViewModel: ReviewsDetailsViewModel
    private lateinit var reviewsDetailsAdapter: CommonAdapter
    private lateinit var reviewsDetailsRecyclerView: RecyclerView

    private lateinit var multiStateContainer: MultiStateContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews_details)
        val sid = intent.getStringExtra("sid")

        multiStateContainer = MultiStatePage.bindMultiState(this, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                reviewsDetailsViewModel.getReviewsDetails(sid!!)
            }
        })

        multiStateContainer.show<LoadingState> { }

        reviewsDetailsViewModel = ViewModelProvider(this).get(ReviewsDetailsViewModel::class.java)

        if (sid != null) {
            reviewsDetailsViewModel.getReviewsDetails(sid)
        }

        initReviewRating()

        initReviewDetails()
    }


    /**
     * 初始化评论详情
     */
    private fun initReviewDetails() {
        reviewsDetailsViewModel.reviewPage.observe(this, Observer {
            if (reviewsDetailsViewModel.reviewPage.value != null) {
                reviewsDetailsAdapter = CommonAdapter.Builder()
                    .setDatas(reviewsDetailsViewModel.reviewPage.value?.list as List<*>)
                    .setLayoutId(R.layout.recycler_reviews_details_item)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            data as Review
                            viewHolder.itemView.findViewById<TextView>(R.id.user_name).text =
                                data.userName
                            viewHolder.itemView.findViewById<RatingBar>(R.id.ratingBar_child).rating =
                                data.rate.toFloat()
                            viewHolder.itemView.findViewById<TextView>(R.id.content).text =
                                data.content.content
                            viewHolder.itemView.findViewById<TextView>(R.id.reviews_gmt_create).text =
                                data.gmtCreate
                            val thumbUpNum = viewHolder.itemView.findViewById<TextView>(R.id.reviews_thumbs_num)
                            thumbUpNum.text = data.praiseNum.toString()

                            val userIcon =
                                viewHolder.itemView.findViewById<ImageView>(R.id.user_icon)
                            val thumbUpIcon = viewHolder.itemView.findViewById<ImageView>(R.id.reviews_details_thumb_up)

                            // 判断是否点赞
                            if (data.praiseStatus == 1) {
                                Glide.with(thumbUpIcon)
                                    .load(R.drawable.ic_thumbs_up_red)
                                    .into(thumbUpIcon)
                            }

                            // 点赞事件监听
                            viewHolder.itemView.findViewById<LinearLayout>(R.id.reviews_thumbs_up).setOnClickListener(object : PerfectClickListener() {
                                override fun onNoDoubleClick(v: View?) {
                                    reviewsDetailsViewModel.addPraise(data.id, data.praiseStatus)
                                    if (data.praiseStatus == 0) {
                                        Glide.with(thumbUpIcon)
                                            .load(R.drawable.ic_thumbs_up_red)
                                            .into(thumbUpIcon)
                                        data.praiseNum ++
                                        data.praiseStatus = 1
                                    } else {
                                        Glide.with(thumbUpIcon)
                                            .load(R.drawable.ic_thumbs_up_black_24  )
                                            .into(thumbUpIcon)
                                        data.praiseNum --
                                        data.praiseStatus = 0
                                    }
                                    thumbUpNum.text = data.praiseNum.toString()
                                }
                            })

                            Glide.with(userIcon)
                                .load(MyApplication.getImageHost() + data.userIcon)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                                .into(userIcon)

                            /* review 内的图片的 recyclerview adapter */
                            if (data.content.images.isNotEmpty()) {
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
                                    viewHolder.itemView.findViewById<RecyclerView>(R.id.review_img_recycler)
                                        .apply {
                                            adapter = reviewImgAdapter
                                            layoutManager = StaggeredGridLayoutManager(
                                                4,
                                                StaggeredGridLayoutManager.VERTICAL
                                            )
                                        }
                            }
                        }
                    })
                    .create()

                findViewById<RecyclerView>(R.id.review_details_recycler).apply {
                    setHasFixedSize(false)
                    layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter = reviewsDetailsAdapter
                }

                multiStateContainer.show<SuccessState> {  }
            }
        })
    }

    /**
     * 初始化评论总分数情况
     */
    private fun initReviewRating() {
        reviewsDetailsViewModel.reviewRate.observe(this, Observer {
            if (reviewsDetailsViewModel.reviewRate.value != null) {
                findViewById<ProgressBar>(R.id.rating_1).progress =
                    reviewsDetailsViewModel.reviewRate.value?.rate1!!
                findViewById<ProgressBar>(R.id.rating_2).progress =
                    reviewsDetailsViewModel.reviewRate.value?.rate2!!
                findViewById<ProgressBar>(R.id.rating_3).progress =
                    reviewsDetailsViewModel.reviewRate.value?.rate3!!
                findViewById<ProgressBar>(R.id.rating_4).progress =
                    reviewsDetailsViewModel.reviewRate.value?.rate4!!
                findViewById<ProgressBar>(R.id.rating_5).progress =
                    reviewsDetailsViewModel.reviewRate.value?.rate5!!.toInt()
                findViewById<TextView>(R.id.sum_rating).text =
                    reviewsDetailsViewModel.reviewRate.value?.sumAverage.toString()

                findViewById<RatingBar>(R.id.reviews_details_all_rating).rating =
                    reviewsDetailsViewModel.reviewRate.value!!.sumAverage.toFloat()
            }
        })
    }

    companion object {
        fun reviewsDetailsStart(context: Context, sid: String) {
            val intent = Intent(context, ReviewsDetailsActivity::class.java)
            intent.putExtra("sid", sid)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}