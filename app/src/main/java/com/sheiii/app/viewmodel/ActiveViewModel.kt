package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.GET_PROMOTION_DATA
import com.sheiii.app.model.PromotionData
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  14:48
 * @description
 */
class ActiveViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    val promotionData = MutableLiveData<PromotionData>()

    /**
     * null
     * @url null
     * @method null
     * @param null
     * @author Zhenqing He
     * @createDate 2021/4/27 14:56
     */
    fun getPromotionData() {
        val params = JSONObject()
//        params.put("plusSizeFlag", 0)
        params.put("promoType", 90)
        params.put("workId", 2)
//        params.put("tabId", 1)

        myVolley.doGetForJsonRequest(
            GET_PROMOTION_DATA,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    Log.d("getPromotionData", result.toString())
                    if (result.getString("state") == "ok") {
                        promotionData.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<PromotionData>() {}.type
                        )
                    }
                }
            })
    }
}