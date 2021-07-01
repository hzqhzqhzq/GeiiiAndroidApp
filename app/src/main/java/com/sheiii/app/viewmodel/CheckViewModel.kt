package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.CHECKOUT_INFO
import com.sheiii.app.constants.GET_CART_NUMBER
import com.sheiii.app.constants.ORDER
import com.sheiii.app.constants.SELECT_PAYMENT
import com.sheiii.app.model.CheckInfoModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  16:30
 * @description
 */
class CheckViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    var checkInfo = MutableLiveData<CheckInfoModel>()
    var orderId = MutableLiveData<String>()
    val cartNumber = MutableLiveData<Int>()

    fun getOrderInfo() {
        val params = JSONObject()
        params.put("from", "address")

        myVolley.doGetForJsonRequest(CHECKOUT_INFO, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
                Log.d("getOrderInfo", result.toString())
                if (result.getString("state") == "ok") {
                    val r = result.getJSONObject("result")
                    if (r.getBoolean("isShow")) {
                        checkInfo.value = MyApplication.getGson().fromJson(r.toString(),
                            object : TypeToken<CheckInfoModel>() {}.type)
                    }
                } else {
                    Log.e("MyRequest", "state code error")
                }
            }
        })
    }

    fun selectPayment(paymentId: Int) {
//        MyApplication.getLoading().showLoading()
        val params = JSONObject()
        params.put("paymentId", paymentId)

        myVolley.doPostForJsonRequest(SELECT_PAYMENT, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                Log.d("selectPayment", result)
                val r = JSONObject(result)
                if (r.getString("state") == "ok") {
                    checkInfo.value?.usePaymentId = paymentId
                }
            }
        })
    }

    /* 下单请求 */
    fun order(addressId: String, userRemark: String, paymentType: Int) {
        val params = JSONObject()

        params.put("addressId", addressId)
        params.put("userRemark", userRemark)
        params.put("paymentType", paymentType)
//        params.put("domain", "https://www.sheiii.com")

        Log.d("order-params", params.toString())

        myVolley.doPostForJsonRequest(ORDER, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                val r = JSONObject(result)
                if (r.getString("state") == "ok") {
                    orderId.value = r.getJSONObject("result").getString("id")
                    getOrderInfo()
                    Log.d("checkResult", result)
                } else {
                    Log.d("checkResult", result)
                }
            }
        })
    }

    /**
     * 获取购物车中的数量
     * @url cart/getCartNumber
     * @method GET
     * @author Zhenqing He
     * @createDate 2021/5/27 9:53
     */
    fun getCartNumber() {
        val params = JSONObject()

        myVolley.doGetForJsonRequest(GET_CART_NUMBER, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
                if (result.getString("state") == "ok") {
                    cartNumber.value = result.getInt("amount")
                }
            }
        })
    }
}