package com.sheiii.app.util

import android.util.Log
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager


/**
 * @author created by Zhenqing He on  17:44
 * @description 用户鉴权
 */
class UserAuth {
    fun isFacebookLogin() : Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
            //        val memberId = MyApplication.getBaseConfig()?.memberId
//        Log.d("accessToken", accessToken.toString())
//        Log.d("memberId", memberId.toString())
        return accessToken != null && !accessToken.isExpired
    }

    fun isLogin() : Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        val memberId = MyApplication.getBaseConfig()?.memberId
//        Log.d("accessToken", accessToken.toString())
        Log.d("memberId", memberId.toString())
        return (accessToken != null && !accessToken.isExpired) || !(memberId.isNullOrEmpty())
    }

    fun facebookLogOut() {
        Log.d("facebookLogOut", "111")
        LoginManager.getInstance().logOut()
//        FacebookSdk.sdkInitialize()
    }
}