package com.sheiii.app

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.CategoryTop
import com.sheiii.app.ui.BadgeActionProvider
import com.sheiii.app.ui.IconNumberView
import com.sheiii.app.ui.MyToolBar
import com.sheiii.app.ui.multistatepage.NetworkErrorState
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.util.UnitConvert
import com.sheiii.app.viewmodel.BaseConfigViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.SuccessState

abstract class BaseActivity : AppCompatActivity() {
    lateinit var baseConfigViewModel: BaseConfigViewModel
    lateinit var toolbar: Toolbar
    lateinit var popupWindow: PopupWindow
    lateinit var popupView: View
    lateinit var multiStateContainer: MultiStateContainer
    lateinit var goCartIconView: BadgeActionProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MyApplication)
        setContentView(getLayoutId())

//        Log.d("ttttt-6", "000")

        multiStateContainer = MultiStatePage.bindMultiState(this, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                recreate()
            }
        })

        checkNetwork()

        baseConfigViewModel = ViewModelProvider(this).get(BaseConfigViewModel::class.java)

        initToolBar()

        initAccountLeftPopup()

        init()

        setToolBar()
    }

    protected abstract fun init()

    protected abstract fun getLayoutId(): Int

    protected abstract fun setToolBar()

    /**
     * ?????????toolbar
     * @author Zhenqing He
     * @createDate 2021/6/24 11:19
     */
    private fun initToolBar() {
        toolbar = findViewById(R.id.my_toolbar)
//        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        /* ????????????????????? */
        baseConfigViewModel.getCartNumber()

        goCartIconView = MenuItemCompat.getActionProvider(toolbar.menu.getItem(0)) as BadgeActionProvider

        baseConfigViewModel.cartNumber.observe(this, Observer {
            MyApplication.setCartNumber(it)
            goCartIconView.updateNumber(it)
        })

        goCartIconView.setClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                MainActivity.actionStartCart(applicationContext, true)
            }
        })
    }

    /**
     * ????????????????????????
     * @author Zhenqing He
     * @createDate 2021/5/27 9:43
     */
    private fun initAccountLeftPopup() {
        popupView = layoutInflater.inflate(R.layout.popup_account_left, null)
        popupWindow = PopupWindow(
            popupView,
            UnitConvert.dp2px(this, 300f),
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )
        popupWindow.setOnDismissListener {
            setBackgroundAlpha(1f)
        }

        baseConfigViewModel.mineInfo.observe(this, Observer {
            if (baseConfigViewModel.mineInfo.value != null) {
                val userIcon = popupView.findViewById<ImageView>(R.id.account_left_user_icon)
                val userName = popupView.findViewById<TextView>(R.id.account_left_user_name)
                if (baseConfigViewModel.mineInfo.value!!.profile == null) {
                    GlideApp.with(applicationContext)
                        .load(baseConfigViewModel.mineInfo.value!!.defPicture).into(
                            userIcon
                        )
                    userName.text = resources.getString(R.string.sign_in)
                } else {
                    GlideApp.with(applicationContext)
                        .load(baseConfigViewModel.mineInfo.value!!.profile.picture).into(
                            userIcon
                        )
                    userName.text = baseConfigViewModel.mineInfo.value!!.profile.nickName
                }

            }
        })

        baseConfigViewModel.topList.observe(this, Observer {
            if (baseConfigViewModel.topList.value != null) {
                val topRecyclerView =
                    popupView.findViewById<RecyclerView>(R.id.account_left_recyclerview)
                topRecyclerView.apply {
                    setHasFixedSize(false)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = CommonAdapter.Builder().setDatas(baseConfigViewModel.topList.value!!)
                        .setLayoutId(R.layout.recycler_account_left_toplist).bindView(
                            object : CommonAdapter.BindView {
                                override fun onBindView(
                                    viewHolder: CommonAdapter.MyViewHolder,
                                    data: Any?,
                                    position: Int
                                ) {
                                    val view = viewHolder.itemView
                                    data as CategoryTop

                                    val topName = view.findViewById<TextView>(R.id.top_name)
                                    topName.text = data.title
                                }
                            }).create()
                }
            }
        })
    }

    /**
     * app ????????????????????????????????????
     * @author Zhenqing He
     * @createDate 2021/6/4 17:50
     */
    private fun checkNetwork() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            //??????1?????????Context.getSystemService(Context.CONNECTIVITY_SERVICE)??????ConnectivityManager??????
            val connMgr = this.getSystemService(CONNECTIVITY_SERVICE) as (ConnectivityManager)

            //??????2?????????ConnectivityManager???????????????NetworkInfo??????
            //NetworkInfo???????????????????????????????????????
            //??????3???????????????????????????????????????
            //??????WIFI???????????????
            var networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val isWifiConn = networkInfo!!.isConnected

            //?????????????????????????????????
            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val isMobileConn = networkInfo!!.isConnected
//            tv_WiFistate.setText("Wifi????????????:$isWifiConn")
//            tv_Network_state.setText("????????????????????????:$isMobileConn")
        } else {
            //??????ConnectivityManager??????
            val connMgr = this.getSystemService(CONNECTIVITY_SERVICE) as (ConnectivityManager)

            //?????????????????????????????????
            val networks: Array<Network> = connMgr.allNetworks

            //??????????????????????????????
            val sb = StringBuilder()

            //??????????????????????????????????????????
            for (i in networks.indices) {
                //??????ConnectivityManager???????????????NetworkInfo??????
                val networkInfo: NetworkInfo? = connMgr.getNetworkInfo(networks[i])
                sb.append(networkInfo?.typeName + " connect is " + networkInfo?.isConnected)
                Log.d("neeeetwork", sb.toString())
            }

            if (networks.indices.isEmpty()) {
                Log.d("neeeetwork", "null")
                multiStateContainer.show<NetworkErrorState> { }
            } else {
                multiStateContainer.show<SuccessState> { }
            }

        }
    }

    /**
     * ????????????????????????
     * @param bgAlpha ??????????????? 0 - 1
     * @author Zhenqing He
     * @createDate 2021/4/19 11:11
     */
    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = this.window.attributes
        lp.alpha = bgAlpha
        if (bgAlpha == 1f) {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //????????????Flag??????,???????????????????????????????????????????????????bug
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //?????????????????????????????????????????????????????????????????????bug
        }
        this.window.attributes = lp
    }
}