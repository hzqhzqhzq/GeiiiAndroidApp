package com.sheiii.app.util

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.sheiii.app.R
import com.sheiii.app.constants.BASE_URL
import com.sheiii.app.constants.VOLLEY_TIMEOUT
import org.json.JSONObject
import java.lang.StringBuilder
import java.util.*


/**
 * @author Zhenqing He
 * @description 封装的volley http请求
 */
class MyVolley {
    companion object {
        private var mRequestQueue: RequestQueue = Volley.newRequestQueue(MyApplication.getInstance())

        fun initMyVolley() {
            mRequestQueue = Volley.newRequestQueue(MyApplication.getInstance())
        }

        fun cancelAll() {
            mRequestQueue.cancelAll(null)
        }
    }

    private fun addRequestQueue(request: Any) {
        if (request is MyJsonObjectGetRequest) {
            request.retryPolicy =
                DefaultRetryPolicy(VOLLEY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            mRequestQueue.add(request)
        } else {
            request as MyJsonObjectPostRequest
            request.retryPolicy =
                DefaultRetryPolicy(VOLLEY_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            mRequestQueue.add(request)
        }
    }

    fun timeOut() {
    }

    /*
    * GET 请求
    * */
    fun doGetForJsonRequest(url: String, params: JSONObject, callback: VolleyJSONObjectCallback) {
        val getUrl = initGetDefaultParams(url, params)
        val jsonObjectRequest = MyJsonObjectGetRequest(Request.Method.GET, BASE_URL + getUrl, null, Response.Listener { response ->
            callback.onSuccess(response)
        }, Response.ErrorListener { error ->
            VolleyLog.d(error.toString())
        })
//        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(5000, 1, 1f)
        addRequestQueue(jsonObjectRequest)
    }

    /*
    * POST 请求
    * */
    fun doPostForJsonRequest(url: String, params: JSONObject, callback: VolleyStringCallback) {
        val deFaultParams = initPostDefaultPrams(params)
        val jsonObjectRequest = MyJsonObjectPostRequest(Request.Method.POST, BASE_URL + url, deFaultParams, Response.Listener { response ->
            callback.onSuccess(response)
        }, Response.ErrorListener { error ->
            VolleyLog.d(error.toString())
        })
//        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(5000, 1, 1f)
        addRequestQueue(jsonObjectRequest)
    }

    /*
    * 初始化默认params参数，默认参数为 siteCode 、 platform
    * */
    private fun initGetDefaultParams(url: String, params: JSONObject) : String {
        var u: String = url
        var flag = "?"
        params.put("siteCode", MyApplication.getInstance().getString(R.string.countryCode))
        params.put("platform", MyApplication.getInstance().getString(R.string.platform))
        params.put("siteId", MyApplication.getInstance().getString(R.string.site_id))

        if (MyApplication.getBaseConfig() != null) {
            params.put("languageCode", MyApplication.getBaseConfig()?.languageCode)
            params.put("currencyCode", MyApplication.getBaseConfig()?.currencyCode)
            params.put("countryCode", MyApplication.getBaseConfig()?.countryCode)
        }


        for (key in params.keys()) {
            u = u + flag + key + "=" + params.get(key)
            flag = "&"
        }

        Log.d("ParamsAll", u.toString())
        return u
    }

    /* 初始化 post 方法参数的默认值 */
    private fun initPostDefaultPrams(params: JSONObject) : JSONObject {
        params.put("siteCode", MyApplication.getInstance().getString(R.string.countryCode))
        params.put("platform", MyApplication.getInstance().getString(R.string.platform))
        params.put("siteId", MyApplication.getInstance().getString(R.string.site_id))

        if (MyApplication.getBaseConfig() != null) {
            params.put("languageCode", MyApplication.getBaseConfig()?.languageCode)
            params.put("currencyCode", MyApplication.getBaseConfig()?.currencyCode)
            params.put("countryCode", MyApplication.getBaseConfig()?.countryCode)
        }

        Log.d("ParamsAll", params.toString())

        return params
    }

    /*
    * Volley 实现自定义headers 需要继承JsonObjectRequest方法，重写getHearders方法，实现自定义headers
    * */
    class MyJsonObjectGetRequest(
            method: Int, url: String, p: JSONObject?,
            listener: Response.Listener<JSONObject>, error: Response.ErrorListener
    ) : com.android.volley.toolbox.JsonObjectRequest(method, url, p, listener, error) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers: MutableMap<String, String> = mutableMapOf()
            headers.putAll(super.getHeaders())
            headers["siteCode"] = MyApplication.getInstance().getString(R.string.countryCode)
            headers["Connection"] = "close"

            /* 设置Cookie注意builder最后一个不需要加; */
            val baseConfig = MyApplication.getBaseConfig()
            val builder: StringBuilder = StringBuilder()
            if (!baseConfig?.memberId.isNullOrEmpty()) {
                builder.append("wt_member_id=${baseConfig?.memberId};")
            } else {
                builder.append("wt_member_id=;")
            }
            if (!baseConfig?.uuid.isNullOrEmpty()) {
                builder.append("wt_uuid=${baseConfig?.uuid};")
            } else {
                builder.append("wt_uuid=;")
            }
            if (!baseConfig?.sessionId.isNullOrEmpty()) {
                builder.append("wt_session_id=${baseConfig?.sessionId};")
            } else {
                builder.append("wt_session_id=;")
            }
            builder.append("wt_currency=${baseConfig?.currencyCode};")
            builder.append("wt_language=${baseConfig?.languageCode};")
            if (headers.containsKey("Cookie")) {
                builder.append("; ");
                builder.append(headers["Cookie"])
            }
//            Log.d("Cookies", builder.toString())
//            Log.d("Headers", headers.toString())
            headers["Cookie"] = builder.toString()

            return headers
        }
    }

    /*
    * Volley StringRequest，重写getHearders方法，实现自定义headers, 重写getParams方法，支持StringRequest发送form data params
    * */
    class MyJsonObjectPostRequest(
            method: Int, url: String, p: JSONObject?,
            listener: Response.Listener<String>, error: Response.ErrorListener
    ) : com.android.volley.toolbox.StringRequest(method, url, listener, error) {
        private val p = p

        override fun getHeaders(): MutableMap<String, String> {
            val headers: MutableMap<String, String> = mutableMapOf()
            headers.putAll(super.getHeaders())
            headers["siteCode"] = MyApplication.getInstance().getString(R.string.countryCode)
            headers["Connection"] = "close"

            /* 设置Cookie注意builder最后一个不需要加; */
            val baseConfig = MyApplication.getBaseConfig()
            val builder: StringBuilder = StringBuilder()
            if (!baseConfig?.memberId.isNullOrEmpty()) {
                builder.append("wt_member_id=${baseConfig?.memberId};")
            } else {
                builder.append("wt_member_id=;")
            }
            if (!baseConfig?.uuid.isNullOrEmpty()) {
                builder.append("wt_uuid=${baseConfig?.uuid};")
            } else {
                builder.append("wt_uuid=;")
            }
            if (!baseConfig?.sessionId.isNullOrEmpty()) {
                builder.append("wt_session_id=${baseConfig?.sessionId};")
            } else {
                builder.append("wt_session_id=;")
            }

            builder.append("wt_currency=${baseConfig?.currencyCode};")
            builder.append("wt_language=${baseConfig?.languageCode}")

            if (headers.containsKey("Cookie")) {
                builder.append("; ");
                builder.append(headers["Cookie"])
            }
            Log.d("Cookies", builder.toString())
            headers["Cookie"] = builder.toString()

            return headers
        }

        override fun getParams(): Map<String, String>? {
            val params: MutableMap<String, String> = HashMap()
            for (key in p!!.keys()) {
                params[key] = p.get(key).toString()
            }
            return params
        }
    }

    /*
    * 请求回调接口 请求成功之后，实现该接口的onSuccess方法，将数据和viewmodel绑定
    * */
    interface VolleyJSONObjectCallback {
        fun onSuccess(result: JSONObject)
    }

    /*
    * 请求回调接口 请求成功之后，实现该接口的onSuccess方法，将数据和viewmodel绑定
    * */
    interface VolleyStringCallback {
        fun onSuccess(result: String)
    }
}