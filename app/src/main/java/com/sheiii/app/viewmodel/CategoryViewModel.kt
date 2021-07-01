package com.sheiii.app.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.GET_FILTER
import com.sheiii.app.constants.GET_SUB_LIST
import com.sheiii.app.constants.GET_TOP_LIST
import com.sheiii.app.constants.SEARCH
import com.sheiii.app.model.*
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

class CategoryViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()

    val topList = MutableLiveData<MutableList<CategoryTop>>()
    val subList = MutableLiveData<MutableList<CategorySub>>()
    val filter = MutableLiveData<FilterModel>()
    val searchResult = MutableLiveData<ProductListModel>()
    val searchProductList = MutableLiveData<MutableList<ProductModel>>()

    /**
     * @description 获取 top 分类
     * @url category/getTopList
     * @method GET
     * @author Zhenqing He
     * @createDate 2021/4/13 17:42
     */
    fun getTopList() {
        val params = JSONObject()

        myVolley.doGetForJsonRequest(
            GET_TOP_LIST,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        topList.value = MyApplication.getGson()
                            .fromJson(
                                result.getJSONArray("result").toString(),
                                object : TypeToken<MutableList<CategoryTop>>() {}.type
                            )
                    }
                }
            })
    }

    /**
     * 获取 SUB 分类
     * @url category/getSubList
     * @method GET
     * @param pid top分类的id
     * @author Zhenqing He
     * @createDate 2021/4/13 17:43
     */
    fun getSubList(pid: Int) {
        val params = JSONObject()
        params.put("pid", pid)

        myVolley.doGetForJsonRequest(
            GET_SUB_LIST,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        subList.value = MyApplication.getGson()
                            .fromJson(
                                result.getJSONArray("result").toString(),
                                object : TypeToken<MutableList<CategorySub>>() {}.type
                            )
                    }
                }
            })
    }

    /**
     * 获取 过滤数据
     * @url category/getFilter
     * @method GET
     * @param categoryId sub分类Id
     * @author Zhenqing He
     * @createDate 2021/4/13 17:44
     */
    fun getFilter(categoryId: Int) {
        val params = JSONObject()
        params.put("categoryId", categoryId)

        myVolley.doGetForJsonRequest(
            GET_FILTER,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        filter.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<FilterModel>() {}.type
                        )
                    }
                }
            })
    }

    /**
     * 根据搜索条件进行搜索
     * @url item/search
     * @method GET
     * @param categoryId sub分类的Id
     * @param sortCode
     * @param attrsList
     * @param pageNumber
     * @author Zhenqing He
     * @createDate 2021/4/13 17:45
     */
    fun search(categoryId: Int, sortCode: String?, attrsList: MutableList<MutableMap<String, String>>?, pageNumber: Int?, plusSizeFlag: Int?) {
        val params = JSONObject()
        params.put("categoryFid", categoryId)
        params.put("sortCode", sortCode)
        params.put("para", null)
        params.put("pageNumber", pageNumber ?: 1)
        params.put("pageSize", 10)
        if (attrsList != null) {
            params.put("attrsList", MyApplication.getGson().toJson(attrsList))
        } else {
            params.put("attrsList", null)
        }
        if (plusSizeFlag != null) {
            params.put("plusSizeFlag", plusSizeFlag)
        }

        Log.d("searchParams", params.toString())

        myVolley.doGetForJsonRequest(SEARCH, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
                Log.d("searchresult", result.toString())
                if (result.getString("state") == "ok") {
                    searchResult.value = MyApplication.getGson().fromJson(
                        result.getJSONObject("result").toString(),
                        object : TypeToken<ProductListModel>() {}.type
                    )
                    val productList: MutableList<ProductModel> = MyApplication.getGson().fromJson(
                        result.getJSONObject("result").getJSONArray("list").toString(),
                        object : TypeToken<MutableList<ProductModel>>() {}.type)
                    if(pageNumber == null) {
                        searchProductList.value = productList
                    } else {
                        searchProductList.value?.addAll(productList)
                        searchProductList.value = searchProductList.value
                    }
                }
            }
        })
    }
}