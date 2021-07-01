package com.sheiii.app.model

data class CartModel(
    val allSelected: Boolean,
    val operateName: String,
    val sectionList: List<Section>,
    val sectionMap: SectionMap,
    val showTips: Boolean,
    val tipsMsg: String,
    val totalBuyNum: Int,
    val totalCouponPrice: Double,
    val totalCouponPriceTxt: String,
    val totalPayPrice: Double,
    val totalPayPriceTxt: String,
    val totalPointPrice: Double,
    val totalPointPriceTxt: String,
    val totalPromoPrice: Double,
    val totalPromoPriceTxt: String,
    val totalSalePrice: Double,
    val totalSalePriceTxt: String,
    val totalShippingPrice: Double,
    val totalShippingPriceTxt: String,
    val totalShowShippingPrice: Double,
    val totalShowShippingPriceTxt: String,
    val totalUsdPayPrice: Double,
    val url: String
)

data class SectionMap(
    val nonPromotionSection: Section,
    val promotionSection: Section
)

data class Section(
    val allSelected: Boolean,
    val blockList: List<Block>,
    val sectionDesc: String,
    val sectionName: String,
    val sectionType: Int,
    val selectDisable: Boolean
)

data class Block(
    val activityLevel: String,
    val activityList: List<Activity>,
    val activityPid: Int,
    val allSelected: Boolean,
    val id: String,
    val operateName: String,
    val promoDesc: String,
    val promoName: String,
    val promoTips: Any,
    val promoType: Int,
    val promoUrl: String,
    val sectionType: Int,
    val selectDisable: Boolean,
    val totalBuyNum: Int,
    val totalCouponPrice: Double,
    val totalPayPrice: Double,
    val totalPayPriceTxt: String,
    val totalPointPrice: Double,
    val totalPromoPrice: Double,
    val totalSalePrice: Double,
    val totalSalePriceTxt: String,
    val workId: Int
)

data class Activity(
    val activityLevel: String,
    val activityPid: Int,
    val id: String,
    val itemList: List<Item>,
    val operateName: String,
    val promoDesc: String,
    val promoName: String,
    val promoTips: Any,
    val promoType: Int,
    val promoUrl: String,
    val totalBuyNum: Int,
    val totalCouponPrice: Double,
    val totalPayPrice: Double,
    val totalPayPriceTxt: String,
    val totalPointPrice: Double,
    val totalPromoPrice: Double,
    val totalSalePrice: Double,
    val totalSalePriceTxt: String,
    val workId: Int
)

data class Item(
    val buyNum: Int,
    val id: String,
    val itemId: Int,
    val itemName: String,
    val itemSid: String,
    val itemSkuId: Int,
    val marketPrice: Double,
    val marketPriceTxt: String,
    val promoType: Int,
    val salePrice: Double,
    val salePriceTxt: String,
    val selected: Int,
    val skuImage: String,
    val skuNames: String,
    val skuValues: String,
    val subTotalBuyWeight: Double,
    val subTotalCouponPrice: Double,
    val subTotalPayPrice: Double,
    val subTotalPointPrice: Double,
    val subTotalPromoPrice: Double,
    val subTotalSalePrice: Double,
    val workId: Int
)

