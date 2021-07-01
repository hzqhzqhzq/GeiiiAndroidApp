package com.sheiii.app.model

data class WishListModel(
    val firstPage: Boolean,
    val lastPage: Boolean,
    val list: List<WishProduct>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPage: Int,
    val totalRow: Int
)

data class WishProduct(
    val image: String,
    val itemId: Int,
    val marketPrice: Int,
    val marketPriceTxt: String,
    val publishStatus: Int,
    val salePrice: Int,
    val salePriceTxt: String,
    val sid: String,
    val title: String,
    val usdPrice: Int
)