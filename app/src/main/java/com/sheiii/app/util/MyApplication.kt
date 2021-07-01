package com.sheiii.app.util

import android.R
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.android.volley.Response.error
import com.google.gson.Gson
import com.sheiii.app.constants.SHEIII_BASE_CONFIG
import com.sheiii.app.model.BaseConfigModel
import com.sheiii.app.model.Contact
import com.sheiii.app.ui.CommonLoadingDialog
import kotlin.properties.Delegates


/**
 * @author Zhenqing He
 * @description 自定义全局变量 实现Application单例化
 */
class MyApplication : Application() {

    companion object {
        private var myInstance: MyApplication by Delegates.notNull()
        private lateinit var myVolley: MyVolley
        private var gson = Gson()
        private var imageHost: String = ""
        private var baseConfig: BaseConfigModel? = null
        private lateinit var loading: CommonLoadingDialog
        private var cartNumber = 0
        private lateinit var currentActivity: Activity

        fun getInstance() = myInstance
        fun getMyVolley(): MyVolley = myVolley
        fun getGson(): Gson = gson
        fun getImageHost(): String = imageHost
        fun setImageHost(newHost: String) {
            imageHost = newHost
        }

        fun getBaseConfig(): BaseConfigModel? = baseConfig
        fun setBaseConfig(value: BaseConfigModel) {
            baseConfig = value

            saveBaseConfig(baseConfig!!)
        }

        fun setLoading(l: CommonLoadingDialog) {
            loading = l
        }

        fun getLoading() = loading

        fun getLoadingByActivity(context: Context) = CommonLoadingDialog.buildDialog(context)

        fun setActivity(activity: Activity) {
            currentActivity = activity
        }

        fun getActivity() = currentActivity

        private fun saveBaseConfig(value: BaseConfigModel) {
            val setting: SharedPreferences =
                myInstance.getSharedPreferences(SHEIII_BASE_CONFIG, 0)
            val editor = setting.edit()
            editor.putString("contact_goUrl", value.contact.goUrl)
            editor.putBoolean("contact_hasChat", value.contact.hasChat)
            editor.putString("contact_iconUrl", value.contact.iconUrl)
            editor.putString("countryCode", value.countryCode)
            editor.putString("currencyCode", value.currencyCode)
            editor.putBoolean("currencyListStatus", value.currencyListStatus)
            editor.putString("languageCode", value.languageCode)
            editor.putBoolean("languageListStatus", value.languageListStatus)
            editor.putString("languageName", value.languageName)
            editor.putString("memberId", value.memberId)
            editor.putString("sessionId", value.sessionId)
            editor.putString("siteCode", value.siteCode)
            editor.putString("uuid", value.uuid)

            editor.apply()
        }

        fun setCartNumber(cartNumber: Int) {
            this.cartNumber = cartNumber
        }

        fun getCartNumber() : Int {
            return this.cartNumber
        }
    }

    override fun onCreate() {
        super.onCreate()
        myInstance = this
        setMyVolley()

        initBaseConfig()
    }

    private fun setMyVolley() {
        myVolley = MyVolley()
        MyVolley.initMyVolley()
    }

    private fun initBaseConfig() {
        val setting: SharedPreferences =
            getSharedPreferences(SHEIII_BASE_CONFIG, Context.MODE_PRIVATE)

        if (setting.contains("uuid")) {
            baseConfig = BaseConfigModel(
                Contact(
                    setting.getString("contact_goUrl", "")!!,
                    setting.getBoolean("contact_hasChat", true),
                    setting.getString("contact_iconUrl", "")!!
                ),
                setting.getString("countryCode", "")!!,
                setting.getString("currencyCode", "")!!,
                setting.getBoolean("currencyListStatus", true),
                setting.getString("languageCode", "")!!,
                setting.getBoolean("languageListStatus", true),
                setting.getString("languageName", "")!!,
                setting.getString("memberId", "")!!,
                setting.getString("sessionId", "")!!,
                setting.getString("siteCode", "")!!,
                setting.getString("uuid", "")!!
            )
        }
    }

}