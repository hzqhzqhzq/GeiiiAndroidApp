package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.*
import com.sheiii.app.model.*
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  17:13
 * @description
 */
class ProductDetailsViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    var thisSid = MutableLiveData<String>("")
    var itemBase = MutableLiveData<ProductDetailsItemBaseModel>()
    val pointTxt = MutableLiveData("")
    val serviceList = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }
    val itemContent = MutableLiveData<List<String>>().apply { value = mutableListOf() }
    val itemReviews = MutableLiveData<ProductDetailsItemReviews>()
    val itemSkuList = MutableLiveData<MutableList<ProductDetailsItemSkuListItem>>().apply {
        value = mutableListOf()
    }
    val itemSize = MutableLiveData<ProductDetailsItemSizeModel>()
    /* 加入购物车之后的提示 */
    val addCartTips = MutableLiveData<String>("")
    val addCartMessage = MutableLiveData<String>("")
    // 服务详情
    val serviceDetails = MutableLiveData<MutableList<ServiceDetails>>()
    // 优惠信息
    val promotion = MutableLiveData<Promotion>()
    // 提示
    val tipsMsgList = MutableLiveData<TipsMsgList>()
    // 猜你喜欢产品列表
    val alsoLikeRecommendList = MutableLiveData<ProductListModel>()
    val soldNumber = MutableLiveData<Int>()
    val productDetails = MutableLiveData<ProductDetailsModel>()
    val cartNumber = MutableLiveData<Int>()

    fun getProductDetails(sid: String) {
        val params = JSONObject()
        params.put("sid", sid)

        thisSid.value = sid

        myVolley.doGetForJsonRequest(
            PRODUCT_DETAIL,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {

                    val r = result.get("result") as JSONObject
                    if (result.get("state") == "ok") {
                        MyApplication.setImageHost(result.getString("imageHost"))
//                        point.value = r.getInt("point")
//                        pointTxt.value = r.getString("pointTxt")

                        if (r.has("soldNum")) {
                            soldNumber.value = r.getInt("soldNum")
                        }

                        productDetails.value = MyApplication.getGson().fromJson(
                            r.toString(),
                            object : TypeToken<ProductDetailsModel>() {}.type
                        )

                        serviceList.value =
                            MyApplication.getGson().fromJson(
                                r.get("serviceList").toString(),
                                object : TypeToken<MutableList<String>>() {}.type
                            )

                        if (r.has("itemContent")) {
//                            Log.d("getProductDetails", r.get("itemContent").toString())
                            itemContent.value = MyApplication.getGson().fromJson(
                                r.get("itemContent").toString(),
                                object : TypeToken<List<String>>() {}.type
                            )
                        }
                        itemReviews.value =
                            MyApplication.getGson().fromJson(
                                r.get("itemReviews").toString(),
                                object : TypeToken<ProductDetailsItemReviews>() {}.type
                            )
//                        Log.d("itemReviews",  r.get("itemReviews").toString())
                        itemSkuList.value =
                            MyApplication.getGson().fromJson(
                                r.get("itemSkuList").toString(),
                                object :
                                    TypeToken<MutableList<ProductDetailsItemSkuListItem>>() {}.type
                            )
                        Log.d("itemSkuListttt", itemSkuList.value.toString())
                        if (r.has("itemSize")) {
                            itemSize.value =
                                MyApplication.getGson().fromJson(
                                    r.get("itemSize").toString(),
                                    object : TypeToken<ProductDetailsItemSizeModel>() {}.type
                                )
                        }
                        itemBase.value =
                            MyApplication.getGson().fromJson(
                                r.get("itemBase").toString(),
                                object : TypeToken<ProductDetailsItemBaseModel>() {}.type
                            )
                        if (r.has("promotion")) {
                            promotion.value =
                                MyApplication.getGson().fromJson(
                                    r.get("promotion").toString(),
                                    object : TypeToken<Promotion>() {}.type
                                )
                        }
                        if (r.has("tipsMsgList")) {
                            tipsMsgList.value = MyApplication.getGson().fromJson(
                                r.get("tipsMsgList").toString(),
                                object : TypeToken<TipsMsgList>() {}.type
                            )
                        }
                    }
                }
            })
    }

    fun addCart(sid: String, skuId: Int, buyNumber: Int) {
        val volley = MyApplication.getMyVolley()
        val params = JSONObject()
        params.put("skuId", skuId)
        params.put("sid", sid)
        params.put("buyNum", buyNumber)
        volley.doPostForJsonRequest(ADD_CART, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                val r = JSONObject(result)
                addCartMessage.value = r.get("message").toString()
                if (addCartMessage.value == "SUCCESS") {
                    addCartTips.value =
                        r.get("tipsMsg").toString().replace("<strong>", "").replace("</strong>", "")
                } else {
                    addCartTips.value = addCartMessage.value
                }
            }
        })
    }

    fun getServiceInfo() {
        val params = JSONObject()
        params.put("code", "service")

        myVolley.doGetForJsonRequest(GET_INFO, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
                if (result.getString("state") == "ok") {
                    val r = result.getJSONObject("result")
                    serviceDetails.value =
                        MyApplication.getGson().fromJson(
                            r.getJSONArray("titleList").toString(),
                            object : TypeToken<MutableList<ServiceDetails>>() {}.type
                        )
                }
            }
        })

    }

    fun addWish(sid: String) {
        MyApplication.getLoading().showLoading()
        val params = JSONObject()

        params.put("sid", sid)

        myVolley.doPostForJsonRequest(WISH_SAVE, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                MyApplication.getLoading().hideLoading()
            }
        })
    }

    fun deleteWish(sid: String) {
        MyApplication.getLoading().showLoading()
        val params = JSONObject()

        params.put("sid", sid)
        myVolley.doPostForJsonRequest(WISH_DELETE, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                MyApplication.getLoading().hideLoading()
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

    /**
     * null
     * @url null
     * @method null
     * @param null
     * @author Zhenqing He
     * @createDate 2021/5/12 17:10
     */
    fun getRecommendList(categoryId: Int, sid: String) {
        val params = JSONObject()

        params.put("categoryId", categoryId)
        params.put("oldAgeFlag", 0)
        params.put("plusSizeFlag", 0)
        params.put("sid", sid)
        params.put(
            "pageNumber",
            if (alsoLikeRecommendList.value?.pageNumber != null) alsoLikeRecommendList.value?.pageNumber!!.plus(
                1
            ) else 1
        )
        params.put("pageSize", 10)
        params.put("from", "detail")

        myVolley.doGetForJsonRequest(
            GET_RECOMMEND,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
//                    Log.d("getRecommendList", result.toString())
                    if (result.getString("state") == "ok") {
                        if (alsoLikeRecommendList.value == null) {
                            alsoLikeRecommendList.value = MyApplication.getGson().fromJson(
                                result.getJSONObject("result").toString(),
                                object : TypeToken<ProductListModel>() {}.type
                            )
                        } else {
                            alsoLikeRecommendList.value!!.list.addAll(
                                MyApplication.getGson().fromJson(
                                    result.getJSONObject("result").getJSONArray("list").toString(),
                                    object : TypeToken<MutableList<ProductModel>>() {}.type
                                )
                            )
                            alsoLikeRecommendList.value!!.pageNumber =
                                result.getJSONObject("result").getInt("pageNumber")
                            alsoLikeRecommendList.value!!.firstPage =
                                result.getJSONObject("result").getBoolean("firstPage")
                            alsoLikeRecommendList.value!!.lastPage =
                                result.getJSONObject("result").getBoolean("lastPage")

                            alsoLikeRecommendList.value = alsoLikeRecommendList.value
                        }


                    }
                }
            })
    }
}