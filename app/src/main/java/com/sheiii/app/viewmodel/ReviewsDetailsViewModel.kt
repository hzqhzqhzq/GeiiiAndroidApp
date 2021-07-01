package com.sheiii.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.ADD_PRAISE
import com.sheiii.app.constants.REVIEWS_DETAILS
import com.sheiii.app.model.ReviewPage
import com.sheiii.app.model.ReviewRate
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  18:03
 * @description
 */
class ReviewsDetailsViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    private val pageNumber = 1
    private val pageSize = 10
    var reviewPage = MutableLiveData<ReviewPage>()
    val reviewRate = MutableLiveData<ReviewRate>()

    val addPraiseResult = MutableLiveData<MutableMap<String, Any>>()

    fun getReviewsDetails(sid: String) {
        val params = JSONObject()
        params.put("sid", sid)
        params.put("pageNumber", pageNumber)
        params.put("pageSize", pageSize)

        myVolley.doGetForJsonRequest(
            REVIEWS_DETAILS,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.get("state") == "ok") {
                        val r = result.get("result") as JSONObject
                        MyApplication.setImageHost(result.getString("imageHost"))
                        reviewPage.value =
                            MyApplication.getGson().fromJson(
                                r.get("reviewPage").toString(),
                                object : TypeToken<ReviewPage>() {}.type
                            )
                        reviewRate.value =
                            MyApplication.getGson().fromJson(
                                r.get("reviewRate").toString(),
                                object : TypeToken<ReviewRate>() {}.type
                            )
                    }
                }
            })
    }

    fun addPraise(id: Int, status: Int) {
        val params = JSONObject()
        params.put("reviewId", id)
        params.put("status", status)

        myVolley.doGetForJsonRequest(
            ADD_PRAISE,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.get("state") == "ok") {
                        addPraiseResult.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<MutableMap<String, Any>>() {}.type
                        )
                    }
                }
            })
    }
}