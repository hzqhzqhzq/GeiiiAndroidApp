package com.sheiii.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.GET_POLICY
import com.sheiii.app.model.MineInfoModel
import com.sheiii.app.model.PolicyModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

/**
 * @author created by Zhenqing He on  18:14
 * @description
 */
class PolicyViewModel : ViewModel() {
    private val myVolley = MyApplication.getMyVolley()
    val policy = MutableLiveData<PolicyModel>()

    fun getPolicy(policyId: String) {
        val params = JSONObject()
        params.put("id", policyId)

        myVolley.doGetForJsonRequest(
            GET_POLICY,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        policy.value = MyApplication.getGson()
                            .fromJson(
                                result.getJSONObject("result").toString(),
                                object : TypeToken<PolicyModel>() {}.type
                            )
                    }
                }
            })
    }

}