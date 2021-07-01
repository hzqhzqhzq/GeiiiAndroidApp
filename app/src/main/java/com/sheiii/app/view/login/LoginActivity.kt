package com.sheiii.app.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.sheiii.app.R
import com.sheiii.app.constants.FACEBOOK_LOGIN
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.MyVolley
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.util.UserAuth
import com.sheiii.app.view.check.CheckActivity
import com.sheiii.app.viewmodel.AccountViewModel
import com.sheiii.app.viewmodel.BaseConfigViewModel
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    private lateinit var baseConfigViewModel: BaseConfigViewModel
    private lateinit var facebookLogin: ConstraintLayout
    private lateinit var directLogin: ConstraintLayout

    private lateinit var from: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        from = intent.getStringExtra("from").toString()

        baseConfigViewModel = ViewModelProvider(this).get(BaseConfigViewModel::class.java)

        facebookLogin = findViewById(R.id.facebook_login)
        directLogin = findViewById(R.id.direct_login)
    }

    override fun onResume() {
        super.onResume()
        facebookLogin.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                FacebookLoginActivity.actionStart(this@LoginActivity)
            }
        })

        directLogin.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                baseConfigViewModel.loginDirect()
            }
        })

        // 登录成功，跳转
        baseConfigViewModel.baseConfig.observe(this, Observer {
            if (baseConfigViewModel.baseConfig.value != null) {
                when(from) {
                    "checkout" -> CheckActivity.actionStart(this)
                }
                finish()
            }
        })
    }

    companion object {
        fun actionStart(context: Context, from: String) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("from", from)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}