package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.*
import com.sheiii.app.model.AddressDetails
import com.sheiii.app.model.AddressFieldModel
import com.sheiii.app.model.AddressListModel
import com.sheiii.app.model.AreaListModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject
import java.util.*

/**
 * @author created by Zhenqing He on  15:17
 * @description
 */
class AddressViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()

    // 地址
    val addressFieldList: MutableLiveData<MutableList<AddressFieldModel>> =
        MutableLiveData<MutableList<AddressFieldModel>>()
//            .apply {
//            value = mutableListOf()
//        }

    // 已经输入的地址数据
    val addressInput: MutableLiveData<MutableMap<String, String>> =
        MutableLiveData<MutableMap<String, String>>().apply {
            value = mutableMapOf()
        }

    // 配送方式
    val deliveryType: MutableLiveData<Int> = MutableLiveData<Int>().apply {
        value = 1
    }

    // 地址选择器的地址域
    val areaList: MutableLiveData<AreaListModel> = MutableLiveData()
    private val nameList: MutableLiveData<String> = MutableLiveData()
    val country: MutableLiveData<String> = MutableLiveData()

    // 用来监听citypicker选择完毕的回调
    val cityPickerCallBack: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    // 城市选择器 结果集map
    val cityPickerResult: MutableLiveData<MutableMap<String, String>> =
        MutableLiveData<MutableMap<String, String>>().apply {
            value = mutableMapOf()
        }
    val saveAddressState: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    // 地址列表
    val addressList: MutableLiveData<AddressListModel> = MutableLiveData()

    // 地址详情
    val addressDetails: MutableLiveData<AddressDetails> = MutableLiveData()

    /* 国家列表 */
    val countryList = MutableLiveData<AreaListModel>()

    /**
     * 获取地址 field 列表
     * @url area/getAddressFieldList
     * @method GET
     * @author Zhenqing He
     * @createDate 2021/4/22 10:48
     */
    fun getAddressFieldList(country: String?) {
        val params = JSONObject()
        if (country != null) {
            params.put("country", country)
        }
        myVolley.doGetForJsonRequest(
            ADDRESS_FIELD,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        MyApplication.setImageHost(result.getString("imageHost"))
                        addressFieldList.value = MyApplication.getGson()
                            .fromJson(
                                result.getJSONArray("result").toString(),
                                object : TypeToken<MutableList<AddressFieldModel>>() {}.type
                            )
                    }
                }
            })
    }

    /**
     * 获取城市地区列表
     * @url area/getList
     * @method GET
     * @param type
     * @author Zhenqing He
     * @createDate 2021/4/22 10:47
     */
    fun getAreaList(type: String) {
        nameList.value = initNameList(type)
        val params = JSONObject()
        params.put("type", type)
        params.put("nameList", nameList.value)
        params.put("deliveryType", deliveryType.value)
        params.put("country", country.value)

        myVolley.doGetForJsonRequest(
            AREA_GET_LIST,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
//                        areaList.value = null
                        areaList.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<AreaListModel>() {}.type
                        )
                    }
                }
            })
    }

    /**
     * 保存地址
     * @url member/address/save
     * @method POST
     * @param id addressId, 地址ID
     * @author Zhenqing He
     * @createDate 2021/4/22 10:46
     */
    fun saveAddress(id: String) {
        val params = JSONObject()
        for (data in addressInput.value!!) {
            params.put(data.key, data.value)
        }

        if (id != "") {
            params.put("addressId", id)
        }

        myVolley.doPostForJsonRequest(SAVE_ADDRESS, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                Log.d("saveAddress", result.toString())
                val r = JSONObject(result)
                saveAddressState.value = r.getString("state") == "ok"
            }
        })
    }

    /**
     * 获取用户地址列表
     * @url member/address/getList
     * @method GET
     * @author Zhenqing He
     * @createDate 2021/4/22 10:45
     */
    fun getAddressList() {
        val params = JSONObject()

        myVolley.doGetForJsonRequest(
            GET_ADDRESS_LIST,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        addressList.value = MyApplication.getGson()
                            .fromJson(
                                result.getJSONObject("result").toString(),
                                object : TypeToken<AddressListModel>() {}.type
                            )
                    }
                }
            })
    }

    /**
     * 获取地址详情
     * @url member/address/getDetail
     * @method GET
     * @param id
     * @param country 国家
     * @author Zhenqing He
     * @createDate 2021/4/22 10:44
     */
    fun getAddressDetails(id: String, country: String) {
        val params = JSONObject()
        params.put("id", id)
        params.put("country", country)

        myVolley.doGetForJsonRequest(
            GET_ADDRESS_DETAILS,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        addressDetails.value = null
                        addressDetails.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<AddressDetails>() {}.type
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
     * @createDate 2021/4/22 11:01
     */
    fun getCountryList() {
        val params = JSONObject()

        myVolley.doGetForJsonRequest(
            GET_COUNTRY_LIST,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        countryList.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<AreaListModel>() {}.type
                        )
                    }
                }
            })
    }

    /**
     * 选择地址
     * @url null
     * @method null
     * @param null
     * @author Zhenqing He
     * @createDate 2021/4/29 11:06
     */
    fun selectAddress(addressId: String) {
        val params = JSONObject()
        params.put("addressId", addressId)

        myVolley.doPostForJsonRequest(SELECT_ADDRESS, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                Log.d("selectAddress", result)
            }
        })
    }

    /**
     * 生成 nameList
     * @param type
     * @return String
     * @author Zhenqing He
     * @createDate 2021/4/22 10:43
     */
    private val stack: Stack<String> = Stack()
    private fun initNameList(type: String): String {
        var nameList = ""
        if (type == "") {
            return ""
        } else {
            for (data in addressFieldList.value!!) {
                if (data.fieldName == type) {
                    nameListRecursion(data.preCode)
                    break
                }
            }
        }
        while (stack.size > 0) {
            nameList = if (nameList != "") {
                "${nameList}@${stack.pop()}"
            } else {
                stack.pop()
            }
        }
        return nameList
    }

    /**
     * 递归获取选中 地址，生成nameList
     * @param preCode
     * @return Boolean
     * @author Zhenqing He
     * @createDate 2021/4/22 10:43
     */
    private fun nameListRecursion(preCode: String): Boolean {
        var checkData: AddressFieldModel? = null
        for (data in addressFieldList.value!!) {
            if (data.fieldName == preCode) {
                stack.push(data.inputValue)
                checkData = data
                break
            }
        }
        if (checkData?.preCode != "") {
            nameListRecursion(checkData?.preCode!!)
        }
        return true
    }
}