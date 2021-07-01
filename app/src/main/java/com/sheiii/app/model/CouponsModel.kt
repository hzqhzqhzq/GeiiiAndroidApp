package com.sheiii.app.model

/**
 * @author created by Zhenqing He on  13:35
 * @description
 */
data class CouponsModel(
    val firstPage: Boolean,
    val lastPage: Boolean,
    val list: List<Coupon>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPage: Int,
    val totalRow: Int,
    var couponStatus: Int
)

data class Coupon(
    val beginTime: String,
    val couponDesc: String,
    val couponName: String,
    val couponPriceText: String,
    val discountType: Int,
    val endTime: String,
    val fullAmount: String,
    val id: String,
    val minusAmount: String,
    val goUrl: String,
    val userFlag: Boolean
)