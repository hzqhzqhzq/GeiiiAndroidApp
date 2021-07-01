package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.CHANGE_CURRENCY
import com.sheiii.app.constants.GET_CURRENCY_LIST
import com.sheiii.app.model.CurrencyModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  11:17
 * @description
 */
class CurrencyViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    val currencyList = MutableLiveData<MutableList<CurrencyModel>>()
    val changeCurrencyResult = MutableLiveData<Boolean>()

    fun getCurrencyList() {
        val params = JSONObject()

        myVolley.doGetForJsonRequest(
            GET_CURRENCY_LIST,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        currencyList.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").getJSONArray("data").toString(),
                            object : TypeToken<MutableList<CurrencyModel>>() {}.type
                        )
                    }
                }
            })
    }

    fun changeCurrency(toCurrency: String) {
        val params = JSONObject()
        params.put("toCurrency", toCurrency)

        myVolley.doPostForJsonRequest(CHANGE_CURRENCY, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                val r = JSONObject(result)
                if (r.getString("state") == "ok") {
//                    for (currency in currencyList.value!!) {
//                        currency.selected = currency.currencyCode == toCurrency
//                    }
                    val baseConfig = MyApplication.getBaseConfig()
                    baseConfig?.currencyCode = toCurrency
//                    MyApplication.setBaseConfig(baseConfig!!)
                    changeCurrencyResult.value = true
                }
            }
        })
    }
}