package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.GET_WISH_LIST
import com.sheiii.app.model.WishListModel
import com.sheiii.app.model.WishProduct
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  14:52
 * @description
 */
class WishListViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    val wishListResult = MutableLiveData<WishListModel>()
    val wishList = MutableLiveData<MutableList<WishProduct>>()

    /**
     * null
     * @url null
     * @method null
     * @param null
     * @author Zhenqing He
     * @createDate 2021/4/20 15:18
     */
    fun getWishList(pagerNumber: Int?) {
        val params = JSONObject()
        params.put("pagerNumber", pagerNumber ?: 1)

        myVolley.doGetForJsonRequest(
            GET_WISH_LIST,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        val r = result.getJSONObject("result")
                        wishListResult.value = MyApplication.getGson()
                            .fromJson(r.toString(), object : TypeToken<WishListModel>() {}.type)
                        val productList: MutableList<WishProduct> = MyApplication.getGson()
                            .fromJson(
                                r.getJSONArray("list").toString(),
                                object : TypeToken<MutableList<WishProduct>>() {}.type
                            )
                        if (pagerNumber != null) {
                            wishList.value?.addAll(productList)
                            wishList.value = wishList.value
                        } else {
                            wishList.value = productList
                        }
                    }
                }
            })
    }
}