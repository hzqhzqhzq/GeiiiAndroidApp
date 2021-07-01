package com.sheiii.app.model

import android.graphics.Color.convert
import com.google.gson.reflect.TypeToken
import com.sheiii.app.util.MyApplication

data class AddressListModel(
    val list: List<AddressDetails>
)

data class AddressDetails(
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
    val selected: Boolean,
    val siteCode: String,
    val status: Int,
    val syncFlag: Int,
    val userName: String,
    val zipcode: String
) {
    //convert a data class to a map
    fun serializeToMap(): Map<String, Any> {
        return convert()
    }

    //convert an object of type I to type O
    private inline fun <I, reified O> I.convert(): O {
        val json = MyApplication.getGson().toJson(this)
        return MyApplication.getGson().fromJson(json, object : TypeToken<O>() {}.type)
    }
}

