package com.sheiii.app

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.sheiii.app.ui.BadgeActionProvider
import com.sheiii.app.ui.CommonLoadingDialog
import com.sheiii.app.ui.IconNumberView
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.util.UserAuth
import com.sheiii.app.viewmodel.BaseConfigViewModel
import com.sheiii.app.viewmodel.ProductDetailsViewModel

class MainActivity : BaseActivity() {
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var productDetailsViewModel: ProductDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MyApplication)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun setToolBar() {
        goCartIconView.setClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                changeNavView(2)
//                goCartIconView.updateNumber(2)
            }
        })

        toolbar.setNavigationIcon(R.drawable.ic_menu)

        toolbar.setNavigationOnClickListener {
            if (baseConfigViewModel.mineInfo.value != null && baseConfigViewModel.mineInfo.value != null) {
                popupWindow.showAsDropDown(findViewById(R.id.my_toolbar))
                setBackgroundAlpha(0.4f)
            } else {
                baseConfigViewModel.getMine()
                baseConfigViewModel.getTopList()
                popupWindow.showAsDropDown(findViewById(R.id.my_toolbar))
                setBackgroundAlpha(0.4f)
            }
        }
    }

    override fun init() {
        navView = findViewById(R.id.nav_view)
        mainCartIconView = goCartIconView
        mainBaseConfigViewModel = baseConfigViewModel

        /* 初始化loading */
        val dialog: CommonLoadingDialog = CommonLoadingDialog.buildDialog(this)
        MyApplication.setLoading(dialog)
        /* 设置当前activity */
        MyApplication.setActivity(this)

        productDetailsViewModel = ViewModelProvider(this).get(ProductDetailsViewModel::class.java)

        /* APP 初始化的时候，检查有没有 BaseConfig 如果没有，则调用接口获取 BaseConfig */
        baseConfigViewModel.getHttpBaseConfig()
        baseConfigViewModel.baseConfig.observe(this, Observer {
            MyApplication.setBaseConfig(it)
            /* baseConfig 没有memberid的时候，退出facebook登录 */
            if (MyApplication.getBaseConfig()?.memberId == null) {
                UserAuth().facebookLogOut()
            }
        })

        /* 配置底部nav bar */
        navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        /* firebase 检索当前token */
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(ContentValues.TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        if (intent?.getBooleanExtra("isGoCart", false)!!) {
            changeNavView(2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    changeNavView(2)
                    goCartIconView.updateNumber(MyApplication.getCartNumber() + 1)
                }
            }
            2 -> {
                if (resultCode == RESULT_OK) {
                    changeNavView(0)
                }
            }
        }
    }

    fun changeNavView(navId: Int) {
        Log.d("111111111","dsjghjdsg")
        navView.selectedItemId = navView.menu.getItem(navId).itemId
    }


    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
//            intent.putExtra("searchUrl", searchUrl)
            context.startActivity(intent)
        }

        fun actionStartCart(context: Context, isGoCart: Boolean) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("isGoCart", isGoCart)
            context.startActivity(intent)
        }

        lateinit var mainCartIconView: BadgeActionProvider

        lateinit var mainBaseConfigViewModel: BaseConfigViewModel

    }

}
