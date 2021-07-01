package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.HOME_INDEX
import com.sheiii.app.constants.ITEM_RECOMMEND
import com.sheiii.app.model.HomeModelList
import com.sheiii.app.model.HomeTabModel
import com.sheiii.app.model.ProductModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

class HomeViewModel : ViewModel() {

    private val myVolley: MyVolley = MyApplication.getMyVolley()

    var firstPage: MutableLiveData<Boolean> = MutableLiveData(true)
    var lastPage: MutableLiveData<Boolean> = MutableLiveData(false)
    var pageNumber: MutableLiveData<Int> = MutableLiveData(1)
    var pageSize: MutableLiveData<Int> = MutableLiveData(10)
    var totalPage: MutableLiveData<Int> = MutableLiveData(0)
    var totalRow: MutableLiveData<Int> = MutableLiveData(0)
    var recommendList: MutableLiveData<MutableList<ProductModel>> = MutableLiveData<MutableList<ProductModel>>().apply {
        value = mutableListOf()
    }
    var moduleList: MutableLiveData<MutableList<HomeModelList>> = MutableLiveData<MutableList<HomeModelList>>().apply {
        value = mutableListOf()
    }
    var tabList: MutableLiveData<MutableList<HomeTabModel>> = MutableLiveData<MutableList<HomeTabModel>>().apply {
        value = mutableListOf()
    }
    var currentTabId: MutableLiveData<Int> = MutableLiveData(1)

    fun initHomePage(index: Int) {
        getDefaultHomePage(index)
        loadRecommendList(index)
    }

    fun getDefaultHomePage(index: Int) {
        val params = JSONObject()
        params.put("index", index)
        myVolley.doGetForJsonRequest(HOME_INDEX, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
//                Log.d("getDefaultHomePage", result.toString())
                if (result.get("state") == "ok") {
                    val r = result.get("result") as JSONObject
                    MyApplication.setImageHost(result.getString("imageHost"))
                    currentTabId.value = r.get("currentTabId") as Int
                    moduleList.value = MyApplication.getGson().fromJson(r.get("moduleList").toString(),
                            object : TypeToken<MutableList<HomeModelList>>() {}.type)
                    tabList.value = MyApplication.getGson().fromJson(r.get("tabList").toString(),
                            object : TypeToken<MutableList<HomeTabModel>>() {}.type)
                }
            }
        })
    }

    fun loadRecommendList(index: Int) {
        if (!lastPage.value!!) {
            var params = JSONObject()
            params.put("pageNumber", pageNumber.value)
            params.put("pageSize", pageSize.value)
            params.put("tabIndex", index)

            myVolley.doGetForJsonRequest(ITEM_RECOMMEND, params, object: MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
//                    Log.d("loadRecommendList", "111111111111111")
                    if (result.get("state") == "ok") {
                        val r = result.getJSONObject("result")
                        MyApplication.setImageHost(result.getString("imageHost"))
                        firstPage.value = r.get("firstPage") as Boolean
                        lastPage.value = r.get("lastPage") as Boolean
                        pageNumber.value = r.get("pageNumber") as Int + 1
                        pageSize.value = r.get("pageSize") as Int
                        totalPage.value = r.get("totalPage") as Int
                        totalRow.value = r.get("totalRow") as Int
                        val prodList: MutableList<ProductModel> = MyApplication.getGson().fromJson(r.get("list").toString(),
                            object: TypeToken<MutableList<ProductModel>>() {}.type)
                        recommendList.value?.addAll(prodList)
                        recommendList.value = recommendList.value

//                        Log.d("loadRecommendList", recommendList.value?.size.toString())
                    }
                }
            })
        }
    }
}