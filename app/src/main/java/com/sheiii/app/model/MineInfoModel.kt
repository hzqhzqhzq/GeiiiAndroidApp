package com.sheiii.app.model

data class MineInfoModel(
    val couponCount: Int,
    val currencySymbol: String,
    val defPicture: String,
    val myOrderCount: List<MyOrderCount>,
    val points: Int,
    val profile: Profile,
    val totalWalletAmount: Int,
    val wishCount: Int
)

data class MyOrderCount(
    val count: Int,
    val icon: String,
    val iconName: String,
    val orderStatus: String
)

data class Profile(
    var birthday: String,
    var email: String,
    val id: String,
    val memberNo: String,
    var nickName: String,
    var phone: String,
    val picture: String,
    var sex: Int,
    val totalPointValue: Int,
    val userName: String,
    val vipRank: Int
)