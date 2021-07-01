package com.sheiii.app.model

data class OrderSuccessRecommendModel(
    val firstPage: Boolean,
    val lastPage: Boolean,
    val list: List<Recommend>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPage: Int,
    val totalRow: Int
)

data class Recommend(
    val discountTxt: String,
    val itemId: Int,
    val marketPrice: String,
    val marketPriceTxt: String,
    val picUrl: String,
    val salePrice: String,
    val salePriceTxt: String,
    val sid: String,
    val tag: String,
    val title: String
)