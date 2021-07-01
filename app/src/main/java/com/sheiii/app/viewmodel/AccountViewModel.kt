package com.sheiii.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.google.gson.reflect.TypeToken
import com.sheiii.app.constants.*
import com.sheiii.app.model.AddressFieldModel
import com.sheiii.app.model.BaseConfigModel
import com.sheiii.app.model.MineInfoModel
import com.sheiii.app.model.ServiceInfoModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import com.sheiii.app.util.VolleyUploadRequest
import org.json.JSONObject
import java.io.File

/**
 * @author created by Zhenqing He on  14:28
 * @description
 */
class AccountViewModel : ViewModel() {
    val mineInfo = MutableLiveData<MineInfoModel>()
    val myVolley = MyApplication.getMyVolley()
    val serviceInfo = MutableLiveData<ServiceInfoModel>()
    val updateProfile = MutableLiveData<Boolean>()
    val updateProfileMsg = MutableLiveData<String>()

    val baseConfig = MutableLiveData<BaseConfigModel>()

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

    fun getServiceInfo(code: String) {
        val params = JSONObject()
        params.put("code", code)

        myVolley.doGetForJsonRequest(
            GET_SUPPORT_INFO,
            params,
            object : MyVolley.VolleyJSONObjectCallback {
                override fun onSuccess(result: JSONObject) {
                    if (result.getString("state") == "ok") {
                        serviceInfo.value = MyApplication.getGson().fromJson(
                            result.getJSONObject("result").toString(),
                            object : TypeToken<ServiceInfoModel>() {}.type
                        )
                    }
                }
            })
    }

    fun updateUserInfo() {
        MyApplication.getLoading().showLoading()
        val params = JSONObject()
        params.put("nickName", mineInfo.value?.profile?.nickName)
        params.put("sex", mineInfo.value?.profile?.sex)
        params.put("birthday", mineInfo.value?.profile?.birthday)
        params.put("phone", mineInfo.value?.profile?.phone)
        params.put("email", mineInfo.value?.profile?.email)

        Log.d("updateUserInfoParams", params.toString())

        myVolley.doPostForJsonRequest(
            UPDATE_PROFILE,
            params,
            object : MyVolley.VolleyStringCallback {
                override fun onSuccess(result: String) {
                    Log.d("updateUserInfo", result)
                    val r = JSONObject(result)
                    updateProfileMsg.value = r.getString("message")
                    updateProfile.value = r.getString("state") == "ok"
                }
            })
    }

    fun uploadAvatar(file: File) {
        MyApplication.getLoading().showLoading()

        val uploadReq = VolleyUploadRequest(UPLOAD_AVATAR, "image", file, "image/jpeg",
            Response.Listener<String> {
                Log.d("allll", it)
                if (it == null || it.isEmpty()) {
                    // 上传失败
                    Log.d("empty", "111")
                }

                val result = JSONObject(it)
                if (result.getString("state") == "ok") {
                    Log.d("ok", it)
                } else {
                    Log.d("fails", it)
                }
                MyApplication.getLoading().hideLoading()
            }, Response.ErrorListener {
                Log.d("ok", it.toString())
                MyApplication.getLoading().hideLoading()
            })
        HttpWorker.getInstance(MyApplication.getInstance()).addToRequestQueue(uploadReq)
    }
}