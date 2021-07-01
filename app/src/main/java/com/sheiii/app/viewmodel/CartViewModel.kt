package com.sheiii.app.viewmodel

import android.app.Application
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.constants.*
import com.sheiii.app.model.CartModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import org.json.JSONObject

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val myVolley = MyApplication.getMyVolley()
    private val myApplication = application
    val cart = MutableLiveData<CartModel>()
    val id = MutableLiveData<String>("")
    val skuValues = MutableLiveData<String>("")
    val itemSkuId = MutableLiveData<Int>(-1)
    val updateSku = MutableLiveData<String>()

    fun getCartList() {
        val params = JSONObject()
        
        myVolley.doGetForJsonRequest(CART_LIST, params, object : MyVolley.VolleyJSONObjectCallback {
            override fun onSuccess(result: JSONObject) {
                Log.d("getCartList", result.toString())
                if (result.get("state") == "ok") {
                    MyApplication.setImageHost(result.getString("imageHost"))
                    cart.value = MyApplication.getGson().fromJson(result.get("result").toString(),
                            object : TypeToken<CartModel>() {}.type)
                }
            }
        })
    }

    fun selectedGroup(selected: Int, sectionType: Int) {
        MyApplication.getLoading().showLoading()

        val params = JSONObject()
        var ids = ""
        if (sectionType == 1) {
//            for (block in cart.value?.sectionMap?.nonPromotionSection?.blockList!!) {
//                for (item in block.itemList) {
//                    ids = if (ids == "") {
//                        item.id
//                    } else {
//                        "$ids,${item.id}"
//                    }
//                }
//            }
        }
        params.put("selected", selected)
        params.put("ids", ids)

        myVolley.doPostForJsonRequest(CART_SELECTED_GROUP, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                val r = JSONObject(result)
                if (r.get("state") == "ok") {
                    MyApplication.setImageHost(r.getString("imageHost"))
                    cart.value = MyApplication.getGson().fromJson(r.get("result").toString(),
                        object : TypeToken<CartModel>() {}.type)
                } else {
                    Log.e("MyVolleyError", r.toString())
                }
            }
        })
    }

    fun updateSku(sid: String, skuId: Int, buyNumber: Int) {
        MyApplication.getLoading().showLoading()

        val params = JSONObject()
        params.put("sid", sid)
        params.put("skuId", skuId)
        params.put("buyNum", buyNumber)
        params.put("id", id.value)

        myVolley.doPostForJsonRequest(CART_UPDATE, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                val r = JSONObject(result)
                if (r.get("state").toString() == "ok") {
                    updateSku.value = r.get("state").toString()
                    getCartList()
                } else {
                    updateSku.value = r.get("message").toString()
                    Log.e("MyVolleyError", r.toString())
                }
            }
        })
    }

    fun updateNumber(buyNum: Int, id: String) {
        MyApplication.getLoading().showLoading()

        val params = JSONObject()
        params.put("buyNum", buyNum)
        params.put("id", id)

        myVolley.doPostForJsonRequest(CART_UPDATE_NUMBER, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                val r = JSONObject(result)
                if (r.get("state") == "ok") {
                    cart.value = MyApplication.getGson().fromJson(r.get("result").toString(),
                        object : TypeToken<CartModel>() {}.type)
//                    MainActivity.mainCartIconView.updateNumber(buyNum + 1)
                    MainActivity.mainBaseConfigViewModel.getCartNumber()
                } else {
                    MyApplication.getLoading().hideLoading()
                    Toast.makeText(
                        myApplication,
                        r.get("message").toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MyVolleyError", r.toString())
                }
            }
        })
    }

    fun deleteProduct(id: String) {
        val successDialog = AlertDialog.Builder(MyApplication.getActivity())
            .setMessage(myApplication.resources.getString(R.string.delete_msg))
            .setTitle(myApplication.resources.getString(R.string.delete_title))
            .setPositiveButton(
                myApplication.resources.getString(R.string.confirm),
                DialogInterface.OnClickListener { _, _ ->
                    MyApplication.getLoading().showLoading()
                    val params = JSONObject()
                    params.put("id", id)

                    myVolley.doPostForJsonRequest(CART_DELETE, params, object : MyVolley.VolleyStringCallback {
                        override fun onSuccess(result: String) {
                            val r = JSONObject(result)
                            if (r.get("state") == "ok") {
                                cart.value = MyApplication.getGson().fromJson(r.get("result").toString(),
                                    object : TypeToken<CartModel>() {}.type)
//                                MainActivity.mainCartIconView.updateNumber(MyApplication.getCartNumber() - 1)
                                MainActivity.mainBaseConfigViewModel.getCartNumber()
                            } else {
                                Log.e("MyVolleyError", r.toString())
                            }
                        }
                    })
                })
            .setCancelable(false)
            .setNegativeButton(
                myApplication.resources.getString(R.string.cancel),
                DialogInterface.OnClickListener { _, _ ->

                })
            .create()
        successDialog.show()
    }

    fun checkProduct(selected: Int, id: String) {
        MyApplication.getLoading().showLoading()
        val params = JSONObject()
        params.put("selected", selected)
        params.put("id", id)

        myVolley.doPostForJsonRequest(CART_SELECTED, params, object : MyVolley.VolleyStringCallback {
            override fun onSuccess(result: String) {
                val r = JSONObject(result)
                if (r.get("state") == "ok") {
                    cart.value = MyApplication.getGson().fromJson(r.get("result").toString(),
                        object : TypeToken<CartModel>() {}.type)
                } else {
                    Log.e("MyVolleyError", r.toString())
                }
            }
        })
    }
}