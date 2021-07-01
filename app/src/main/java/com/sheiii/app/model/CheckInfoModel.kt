package com.sheiii.app.model

data class CheckInfoModel(
    val address: Address,
    val cartBlock: CartBlock,
    val couponCode: String,
    val couponList: List<Coupon>,
    val couponTips: String,
    val currencySymbol: String,
    val isShow: Boolean,
    val paymentList: List<Payment>,
    val useDiscountType: Int,
    var usePaymentId: Int
)

data class Address(
    val address1: String,
    val address2: String,
    val addressTxt: String,
    val addressTxtList: List<String>,
    val city: String,
    val country: String,
    val countryCode: String,
    val deliveryType: Int,
    val district: String,
    val email: String,
    val firstName: String,
    val gmtCreate: String,
    val gmtUpdate: String,
    val id: String,
    val isDefault: Int,
    val lastName: String,
    val marketType: String,
    val memberId: String,
    val middleName: String,
    val mobile: String,
    val province: String,
    val siteCode: String,
    val status: Int,
    val syncFlag: Int,
    val userName: String,
    val zipcode: String
)

data class CartBlock(
    val itemList: List<CartBlockItem>,
    val showTips: Boolean,
    val tipsMsg: Any,
    val totalBuyNum: Int,
    val totalCouponPrice: Int,
    val totalCouponPriceTxt: String,
    val totalPayPrice: Double,
    val totalPayPriceTxt: String,
    val totalPayUsdPrice: Double,
    val totalPointPrice: Double,
    val totalPointPriceTxt: String,
    val totalPromoPrice: Double,
    val totalPromoPriceTxt: String,
    val totalSalePrice: Double,
    val totalSalePriceTxt: String,
    val totalShippingPrice: Double,
    val totalShippingPriceTxt: String,
    val totalWalletPrice: Double,
    val totalWalletPriceTxt: String,
    val useWalletPrice: Double,
    val useWalletPriceMax: Int,
    val useWalletPriceMaxTxt: String,
    val useWalletPriceTxt: String
)

data class Payment(
    val iconUrl: String,
    val paymentId: Int,
    val paymentName: String,
    var isCheck: Boolean = false
)

data class CartBlockItem(
    val buyNum: Int,
    val id: String,
    val itemId: Int,
    val itemName: String,
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