package com.sheiii.app.model

/**
 * @author created by Zhenqing He on  17:46
 * @description 评论详情 model
 */
data class ReviewsDetailsModel(
    val reviewPage: ReviewPage,
    val reviewRate: ReviewRate
)

data class ReviewPage(
    val firstPage: Boolean,
    val lastPage: Boolean,
    val list: List<Review>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPage: Int,
    val totalRow: Int
)

data class ReviewRate(
    val rate1: Int,
    val rate2: Int,
    val rate3: Int,
    val rate4: Int,
    val rate5: Double,
    val sumAverage: Double,
    val sumCount: Int,
    val sumRate: Int
)

data class Review(
    val content: ReviewsContent,
    val gmtCreate: String,
    val id: Int,
    var praiseNum: Int,
    var praiseStatus: Int,
    val rate: Int,
    val reply: String,
    val userIcon: String,
    val userName: String
)