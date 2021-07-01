package com.sheiii.app.model

data class HomeModelList(
    val `data`: Data,
    val id: Int,
    val title: String,
    val typeId: Int
)

data class Data(
    val imageCount: Int,
    val imageList: List<Image>,
    val banner: String,
    val goUrl: String,
    val leftSeconds: Int,
    val itemList: MutableList<HomeModuleProduct>
)

data class Image(
    val id: Int,
    val imgUrl: String,
    val title: String,
    val url: String
)

data class HomeModuleProduct(
    val categoryId1: Int,
    val discountTxt: String,
    val id: Int,
    val marketPrice: Double,
    val marketPriceTxt: String,
    val picUrl: String,
    val plusSizeFlag: Int,
    val promoType: Int,
    val salePrice: Double,
    val salePriceTxt: String,
    val sid: String,
    val title: String,
    val workId: Int
)