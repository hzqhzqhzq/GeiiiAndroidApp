package com.sheiii.app.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.*
import com.sheiii.app.model.*
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  16:25
 * @description
 */
class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val myVolley = MyApplication.getMyVolley()
    var orderInfo = MutableLiveData<OrderSuccessModel>()
    val recommendList = MutableLiveData<OrderSuccessRecommendModel>()
    val orderTabsList = MutableLiveData<MutableList<OrderTabs>>()
    val orderTabsDataMap = MutableLiveData<MutableMap<String, OrderList>>().apply {
        value = mutableMapOf()
    }
    val orderList = MutableLiveData<OrderList>()

    val orderDetails = MutableLiveData<OrderDetails>()

    fun getSuccessInfo(orderId: String) {
        val params = JSONObject()
        params.put("id", orderId)
//        params.put("domain", "https://www.sheiii.com")

        myVolley.doGetForJsonRequest(
            CHECKOUT_SUCCESS,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        orderInfo.value = MyApplication.getGson()
                            .fromJson(
                                result.getJSONObject("result").toString(),
                                object : TypeToken<OrderSuccessModel>() {}.type
                            )
                    }
                }
            })
    }

    fun getOrderSuccessRecommend() {
        val params = JSONObject()
        params.put("from", "orderSuccess")

        myVolley.doGetForJsonRequest(
            GET_RECOMMEND,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        recommendList.value = MyApplication.getGson()
                            .fromJson(
                                result.getJSONObject("result").toString(),
                                object : TypeToken<OrderSuccessRecommendModel>() {}.type
                            )
                    }
                }
            })
    }

    fun getOrderTabs(orderStatus: String) {
        val params = JSONObject()
        params.put("orderStatus0", orderStatus)

        myVolley.doGetForJsonRequest(
            GET_ORDER_TABS,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    Log.d("getOrderTabs", result.toString())
                    if (result.getString("state") == "ok") {
                        orderTabsList.value = MyApplication.getGson().fromJson(
                            result.getJSONArray("result").toString(),
                            object : TypeToken<MutableList<OrderTabs>>() {}.type
                        )
                    }
                }
            })
    }

    fun getOrderList(state: String) {
        val params = JSONObject()
        params.put(
            "pageNumber",
            if (orderTabsDataMap.value?.get(state) != null) orderTabsDataMap.value?.get(state)?.pageNumber!!.plus(
                1
            ) else 1
        )
        params.put("pageSize", 5)
        params.put("orderStatus", state)



        myVolley.doGetForJsonRequest(
            GET_ORDER_LIST,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onSuccess(result: JSONObject) {
//                    Log.d("getOrderList", result.toString())
                    if (result.getString("state") == "ok") {

                        if (orderTabsDataMap.value?.get(state) == null) {
                            val data = MyApplication.getGson().fromJson(
                                result.getJSONObject("result").toString(),
                                object : TypeToken<OrderList>() {}.type
                            ) as OrderList
                            orderTabsDataMap.value?.put(state, data)

                        } else {
                            val data =  MyApplication.getGson().fromJson(
                                result.getJSONObject("result").toString(),
                                object : TypeToken<OrderList>() {}.type
                            ) as OrderList

                            orderTabsDataMap.value?.get(state)?.list?.addAll(data.list)
                            orderTabsDataMap.value?.get(state)?.pageNumber = data.pageNumber
                            orderTabsDataMap.value?.get(state)?.lastPage = data.lastPage

                        }

                        orderList.value =
                            MyApplication.getGson().fromJson(
                                result.getJSONObject("result")
                                    .toString(),
                                object : TypeToken<OrderList>() {}.type
                            )
                    } else {
                        Log.d("volley error", result.toString())
                    }
                }
            })
    }

    fun getOrderDetails(id: String) {
        val params = JSONObject()
        params.put("id", id)
//        params.put("domain", "https://www.sheiii.com")

        myVolley.doGetForJsonRequest(
            GET_ORDER_DETAILS,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        orderDetails.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<OrderDetails>() {}.type
                        )
                    }
                }
            })
    }
}