package com.sheiii.app.model

data class Promotion(
    val itemSkuList: List<ItemSku>,
    val leftSeconds: Int,
    val promoName: String,
    val promoTipList: List<String>,
    val promoType: Int,
    val promoUrl: String,
    val salePrice: Double,
    val salePriceTxt: String
)

data class ItemSku(
    val marketPrice: Double,
    val marketPriceTxt: String,
    val propPath: String,
    val quantity: Int,
    val salePrice: Double,
    val salePriceTxt: String,
    val skuId: Int
)

data class PromotionData(
    val categoryData: List<CategoryData>,
    val     promotionData: PromotionDataX
)

data class CategoryData(
    val id: Int,
    val selected: Boolean,
    val title: String
)

data class PromotionDataX(
    val activityDesc: String,
    val activityLevel: String,
    val activityName: String,
    val activityPid: Int,
    val beginTime: String,
    val discountAmount: Int,
    val endTime: String,
    val id: Int,
    val promoType: Int,
    val imgUrl: String,
    val imgUrlPc: String,
    val title: String,
    val titleDesc: String
)