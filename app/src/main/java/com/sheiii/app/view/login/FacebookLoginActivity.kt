package com.sheiii.app.view.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.sheiii.app.R
import com.sheiii.app.constants.FACEBOOK_LOGIN
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import com.sheiii.app.util.UserAuth
import com.sheiii.app.viewmodel.BaseConfigViewModel
import org.json.JSONObject

class FacebookLoginActivity : AppCompatActivity() {
    lateinit var callbackManager: CallbackManager
    private lateinit var loginButton: LoginButton
    private lateinit var baseConfigViewModel: BaseConfigViewModel
    private val userAuth: UserAuth = UserAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook_login)

        callbackManager = CallbackManager.Factory.create()

        loginButton = findViewById(R.id.login_button)
        loginButton.setReadPermissions(listOf("public_profile", "email"))

        baseConfigViewModel = ViewModelProvider(this).get(BaseConfigViewModel::class.java)
        baseConfigViewModel.baseConfig.value = MyApplication.getBaseConfig()

        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
                val params = JSONObject()
                params.put("userId", AccessToken.getCurrentAccessToken().userId)
                params.put("inputToken", AccessToken.getCurrentAccessToken().token)

                MyApplication.getMyVolley().doPostForJsonRequest(
                    FACEBOOK_LOGIN, params,
                    object : MyVolley.VolleyStringCallback {
                        override fun onSuccess(result: String) {
                            val r = JSONObject(result)
                            if (r.get("state") == "ok") {
                                baseConfigViewModel.baseConfig.value?.memberId = r.getString("memberId")
                                baseConfigViewModel.baseConfig.value?.sessionId = r.getString("sessionId")
                                MyApplication.setBaseConfig(baseConfigViewModel.baseConfig.value!!)
                            } else {
                                Log.d("loginResult", r.getString("message"))
                            }
                            finish()
                        }
                    })
            }

            override fun onCancel() {
                // App code
                Log.d("loginResult", "cancel")
            }

            override fun onError(exception: FacebookException) {
                // App code
                Log.d("loginResult", exception.toString())
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, FacebookLoginActivity::class.java)
//            intent.putExtra("sid", product.sid)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}