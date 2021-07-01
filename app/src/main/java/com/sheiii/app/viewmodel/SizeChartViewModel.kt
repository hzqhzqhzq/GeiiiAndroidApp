package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.SIZE_CHART
import com.sheiii.app.model.ItemSize
import com.sheiii.app.model.SizeExplanation
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  15:13
 * @description
 */
class SizeChartViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    var itemSize = MutableLiveData<ItemSize>()
    var sizeExplanationList: MutableLiveData<MutableList<SizeExplanation>> = MutableLiveData<MutableList<SizeExplanation>>().apply {
        value = mutableListOf()
    }

    fun getSizeChart(sid: String) {
        val params = JSONObject()
        params.put("sid", sid)

        myVolley.doGetForJsonRequest(SIZE_CHART, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
                if (result.get("state") == "ok") {
                    val r = result.get("result") as JSONObject
//                    Log.d("sizeExplanation", r.get("sizeExplanation").toString())
                    itemSize.value = MyApplication.getGson().fromJson(r.get("itemSize").toString(),
                        object : TypeToken<ItemSize>() {}.type)
                    val list: MutableList<SizeExplanation> = MyApplication.getGson().fromJson(r.get("sizeExplanation").toString(),
                            object : TypeToken<MutableList<SizeExplanation>>() {}.type)
                    sizeExplanationList.value?.addAll(list)
                    sizeExplanationList.value = sizeExplanationList.value
                }
            }
        })
    }
}