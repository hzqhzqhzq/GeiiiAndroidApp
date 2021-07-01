package com.sheiii.app.model

data class FlashDealModel(
    val itemData: ItemData,
    val leftSeconds: Int,
    val title: String,
    val titleDesc: String
)

data class ItemData(
    val firstPage: Boolean,
    val lastPage: Boolean,
    val list: List<FlashDealProduct>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPage: Int,
    val totalRow: Int
)

data class FlashDealProduct(
    val discountTxt: String,
    val image: String,
    val itemId: Int,
    val marketPrice: Int,
    val marketPriceTxt: String,
    val salePrice: Int,
    val salePriceTxt: String,
    val sid: String,
    val soldNum: Int,
    val title: String,
    val totalStock: Int,
    val usdPrice: Double
)