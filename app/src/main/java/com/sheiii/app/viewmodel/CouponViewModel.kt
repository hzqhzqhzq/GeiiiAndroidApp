package com.sheiii.app.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.GET_COUPON_PAGE
import com.sheiii.app.model.Coupon
import com.sheiii.app.model.CouponsModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  09:02
 * @description
 */
class CouponViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    val couponPager = MutableLiveData<CouponsModel>()

    val couponMap = MutableLiveData<MutableMap<Int, List<Coupon>>>().apply {
        value = mutableMapOf()
    }


    /**
     * null
     * @url null
     * @method null
     * @param null
     * @author Zhenqing He
     * @createDate 2021/4/23 11:51
     */
    fun getCouponPage(couponStatus: Int) {
        val params = JSONObject()
        params.put("couponStatus", couponStatus)
        params.put("pageNumber", 1)

        myVolley.doGetForJsonRequest(
            GET_COUPON_PAGE,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onSuccess(result: JSONObject) {
                    Log.d("ffffffffffff", result.getJSONObject("result").toString())
                    if (result.getString("state") == "ok") {
                        val tempList: List<Coupon> = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").getJSONArray("list").toString(),
                            object : TypeToken<List<Coupon>>() {}.type
                        )
                        if (couponMap.value?.get(couponStatus) == null) {
                            couponMap.value?.put(
                                couponStatus, tempList
                            )
                        } else {
                            couponMap.value?.replace(couponStatus, tempList)
                        }

                        couponPager.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<CouponsModel>() {}.type
                        )
                        couponPager.value?.couponStatus = couponStatus
                    }
                }
            })
    }

    /**
     * null
     * @url null
     * @method null
     * @param null
     * @author Zhenqing He
     * @createDate 2021/4/23 11:39
     */
//    fun getCampaignCouponList() {
//        val params = JSONObject()
//        myVolley.doGetForJsonRequest("member/coupon/getCampaignCouponList", params, object : MyVolley.VolleyJSONObjectCallback {
//            override fun onSuccess(result: JSONObject) {
//                Log.d("getCampaignCouponList", result.toString())
//            }
//        })
//    }

    /**
     * null
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/23 11:48
     */
//    fun receiveCampaignCounpon(id: String) {
//        val params = JSONObject()
//        params.put("id", id)
//
//        myVolley.doPostForJsonRequest("member/coupon/receiveCampaignCounpon", params, object : MyVolley.VolleyStringCallback {
//            override fun onSuccess(result: String) {
//            }
//        })
//    }
}