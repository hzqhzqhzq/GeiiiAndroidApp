package com.sheiii.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.GET_FLASH_DEAL_PAGE
import com.sheiii.app.constants.GET_PROMOTION_DATA
import com.sheiii.app.model.FlashDealModel
import com.sheiii.app.model.PromotionData
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  12:01
 * @description
 */
class FlashDealViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    val promotionData = MutableLiveData<PromotionData>()
    val flashPage = MutableLiveData<FlashDealModel>()

    /**
     * null
     * @url null
     * @method null
     * @param null
     * @author Zhenqing He
     * @createDate 2021/4/26 15:14
     */
    fun getFlashPage() {
        val params = JSONObject()
        params.put("plusSizeFlag", 0)
        params.put("pageNumber", 1)
        params.put("pageSize", 10)
        params.put("workId", 24)
        params.put("tabId", 1)

        myVolley.doGetForJsonRequest(
            GET_FLASH_DEAL_PAGE,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        flashPage.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<FlashDealModel>() {}.type
                        )
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
     * @createDate 2021/4/26 15:14
     */
    fun getPromotionData() {
        val params = JSONObject()
        params.put("plusSizeFlag", 0)
        params.put("promoType", 10)
        params.put("workId", 24)
        params.put("tabId", 1)

        myVolley.doGetForJsonRequest(
            GET_PROMOTION_DATA,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
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