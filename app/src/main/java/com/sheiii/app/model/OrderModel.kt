package com.sheiii.app.model

/**
 * @author created by Zhenqing He on  16:29
 * @description
 */
data class OrderSuccessModel(
    val addressTxt: String,
    val itemList: List<OrderSuccessItem>,
    val orderNo: String,
    val payPriceTxt: String,
    val payUsdPrice: Double,
    val tipMessage: String,
    val userName: String,
    val userPhone: String
)

data class OrderSuccessItem(
    val buyNum: Int,
    val currencyCode: String,
    val id: String,
    val image: String,
    val itemId: Int,
    val itemSkuId: Int,
    val salePrice: Int,
    val salePriceTxt: String,
    val sid: String,
    val skuName: String,
    val title: String,
    val usdPrice: Double
)

data class OrderTabs(
    val id: Int,
    val orderStatus: String,
    val selected: Boolean,
    val tabName: String
)

data class OrderList(
    val firstPage: Boolean,
    var lastPage: Boolean,
    val list: MutableList<Order>,
    var pageNumber: Int,
    val pageSize: Int,
    val totalPage: Int,
    val totalRow: Int
)

data class Order(
    val currencyCode: String,
    val gmtCreate: String,
    val hidePayMenu: Int,
    val hideReviewMenu: Int,
    val itemList: List<OrderItem>,
    val leftSeconds: Int,
    val orderId: String,
    val orderNo: String,
    val orderStatus: String,
    val orderStatusTxt: String,
    val paymentId: Int,
    val reviewStatus: Int,
    val totalBuyNum: Int,
    val totalPayPrice: Int,
    val totalPayPriceTxt: String
)

data class OrderItem(
    val buyNum: Int,
    val currencyCode: String,
    val id: String,
    val image: String,
    val itemId: Int,
    val itemName: String,
    val itemSkuId: Int,
    val salePrice: Int,
    val salePriceTxt: String,
    val sid: String,
    val skuName: String,
    val subTotalPayPrice: Int,
    val usdPrice: Double
)

data class OrderDetails(
    val consignee: Consignee,
    val gmtCreate: String,
    val hidePayMenu: Int,
    val hideReviewMenu: Int,
    val itemList: List<OrderItem>,
    val leftSeconds: Int,
    val orderNo: String,
    val orderStatus: String,
    val orderStatusTxt: String,
    val orderTime: Any,
    val payStatus: Int,
    val paymentId: Int,
    val paymentList: List<Payment>,
    val paymentListStatus: Boolean,
    val paymentTypeTxt: String,
    val totalBuyNum: Int,
    val totalCouponPriceTxt: String,
    val totalPayPriceTxt: String,
    val totalPointPriceTxt: String,
    val totalPromoPriceTxt: String,
    val totalSalePriceTxt: String,
    val totalShippingPriceTxt: String,
    val totalWalletPriceTxt: String
)

data class Consignee(
    val address1: String,
    val address2: String,
    val addressId: String,
    val addressTxt: String,
    val addressTxtList: List<String>,
    val city: String,
    val country: String,
    val countryCode: String,
    val deliveryType: Int,
    val district: String,
    val email: String,
    val firstName: String,
    val fullName: String,
    val gmtCreate: String,
    val gmtUpdate: String,
    val lastName: String,
    val marketType: String,
    val mobile: String,
    val orderId: String,
    val province: String,
    val siteCode: String,
    val zipcode: String
)