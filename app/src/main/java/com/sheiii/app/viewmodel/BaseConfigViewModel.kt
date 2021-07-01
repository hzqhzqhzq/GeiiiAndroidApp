package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.*
import com.sheiii.app.model.BaseConfigModel
import com.sheiii.app.model.CategoryTop
import com.sheiii.app.model.MineInfoModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  09:10
 * @description
 */
class BaseConfigViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    val baseConfig = MutableLiveData<BaseConfigModel>()
    val cartNumber = MutableLiveData<Int>()
    val mineInfo = MutableLiveData<MineInfoModel>()
    val topList = MutableLiveData<MutableList<CategoryTop>>()

    /**
     * 获取baseconfig信息
     * @url ite/getBaseConfig
     * @method GET
     * @author Zhenqing He
     * @createDate 2021/5/27 9:53
     */
    fun getHttpBaseConfig() {
        val params = JSONObject()
//        params.put("domain", "www.sheiii.com")

        params.put("domain", "th.sheiii.com")
        myVolley.doGetForJsonRequest(BASE_CONFIG, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
//                Log.d("getHttpBaseConfig", result.toString())
                if (result.get("state") == "ok") {
                    MyApplication.setImageHost(result.getString("imageHost"))
                    baseConfig.value = MyApplication.getGson().fromJson(result.get("result").toString(),
                            object : TypeToken<BaseConfigModel>() {}.type)
                }
            }
        })
    }

    /**
     * 直接登录
     * @url user/loginWithAnonymous
     * @method POST
     * @author Zhenqing He
     * @createDate 2021/5/27 9:53
     */
    fun loginDirect() {
        val params = JSONObject()

        myVolley.doPostForJsonRequest(LOGIN_DIRECT, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                val r = JSONObject(result)
                Log.d("loginDirect", result)
                baseConfig.value = MyApplication.getBaseConfig()
                if (r.getString("state") == "ok") {
                    baseConfig.value?.memberId = r.getString("memberId")
                    baseConfig.value?.sessionId = r.getString("sessionId")
                    MyApplication.setBaseConfig(baseConfig.value!!)
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

    /**
     * 获取用户个人信息
     * @url member/account/getMine
     * @method GET
     * @author Zhenqing He
     * @createDate 2021/5/27 9:55
     */
    fun getMine() {
        val params = JSONObject()

        myVolley.doGetForJsonRequest(GET_MINE, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
                Log.d("getMine", result.toString())
                if (result.getString("state") == "ok") {
                    mineInfo.value = MyApplication.getGson()
                        .fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<MineInfoModel>() {}.type
                        )
                }
            }
        })
    }

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

}