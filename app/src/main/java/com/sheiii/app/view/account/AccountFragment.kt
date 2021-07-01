package com.sheiii.app.view.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.header.FalsifyFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.Activity
import com.sheiii.app.model.MyOrderCount
import com.sheiii.app.ui.multistatepage.MyLoadingState
import com.sheiii.app.util.AuthCheckClickListener
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.util.UserAuth
import com.sheiii.app.view.address.AddressActivity
import com.sheiii.app.view.coupon.CouponActivity
import com.sheiii.app.view.login.FacebookLoginActivity
import com.sheiii.app.view.order.OrderProcessActivity
import com.sheiii.app.view.point.PointActivity
import com.sheiii.app.view.policy.PolicyActivity
import com.sheiii.app.view.wallet.WalletActivity
import com.sheiii.app.view.wishlist.WishListActivity
import com.sheiii.app.viewmodel.AccountViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState


/**
 * @author created by Zhenqing He on  18:15
 * @description
 */
class AccountFragment : Fragment() {
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var root: View

    private val userAuth: UserAuth = UserAuth()

    private lateinit var orderStateRecyclerView: RecyclerView

    private lateinit var multiStateContainer: MultiStateContainer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_account, container, false)
        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        multiStateContainer = MultiStatePage.bindMultiState(root, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                accountViewModel.getMine()
            }
        })
        multiStateContainer.show<MyLoadingState> { }

        orderStateRecyclerView = root.findViewById(R.id.account_order_state_recycler)
        return multiStateContainer
    }

    override fun onResume() {
        super.onResume()

        accountViewModel.getMine()
        /* 条款按钮监听 */
        setPolicyListener()
        /* 修改货币监听 */
        setChangeCurrencyListener()
        /* 订单流程 事件监听 */
        setOrderProcessListener()
        /* 个人服务事件监听 */
        setUserServerListener()
        /* 收藏列表点击监听 */
        setWishListListener()

        setPointListener()

        setWalletListener()

        setCouponListener()

        accountViewModel.mineInfo.observe(viewLifecycleOwner, Observer {
            if (accountViewModel.mineInfo.value != null) {
                // 初始化用户数据
                initUserInfo()
                multiStateContainer.show<SuccessState> { }
            }
        })
    }

    /**
     * 初始化用户信息
     * @author Zhenqing He
     * @createDate 2021/4/20 13:59
     */
    private fun initUserInfo() {
        val data = accountViewModel.mineInfo.value!!

        val userIcon = root.findViewById<ImageView>(R.id.account_user_icon)
        Glide.with(userIcon)
            .load(data.defPicture)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(40)))
            .into(userIcon)

        root.findViewById<TextView>(R.id.coupons_num).text = data.couponCount.toString()
        root.findViewById<TextView>(R.id.wishlist_num).text = data.wishCount.toString()
        root.findViewById<TextView>(R.id.wallet_num).text = data.totalWalletAmount.toString()
        root.findViewById<TextView>(R.id.points_num).text = data.points.toString()

        if (data.profile != null) {
            root.findViewById<TextView>(R.id.account_user_name).text = data.profile.nickName
        } else {
            root.findViewById<TextView>(R.id.account_user_name).text =
                requireContext().resources.getString(R.string.sign_in)
        }

        orderStateRecyclerView.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = StaggeredGridLayoutManager(
                data.myOrderCount.size,
                StaggeredGridLayoutManager.VERTICAL
            )
            adapter =
                CommonAdapter.Builder()
                    .setDatas(accountViewModel.mineInfo.value!!.myOrderCount as List<*>)
                    .setLayoutId(R.layout.recycler_account_order_state_item)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            data as MyOrderCount

                            viewHolder.itemView.layoutParams.width

                            val image =
                                viewHolder.itemView.findViewById<ImageView>(R.id.account_user_icon)
                            viewHolder.itemView.findViewById<TextView>(R.id.account_order_state_name).text =
                                data.iconName
                            Glide.with(image)
                                .load(data.icon)
                                .into(image)

                            viewHolder.itemView.findViewById<ConstraintLayout>(R.id.account_order_state)
                                .setOnClickListener(object : AuthCheckClickListener() {
                                    override fun onNoDoubleClick(v: View?) {
                                        OrderProcessActivity.actionStart(
                                            root.context,
                                            data.orderStatus
                                        )
                                    }
                                })
                        }
                    }).create()
        }

        // 修改用户信息点击监听
        root.findViewById<ConstraintLayout>(R.id.account_user_info)
            .setOnClickListener(object : AuthCheckClickListener() {
                override fun onNoDoubleClick(v: View?) {
                    UserInfoActivity.actionStart(requireContext())
                }
            })

        // 检查是否登录，登录则显示登出功能
        if (!checkUserAuth()) {
            root.findViewById<TextView>(R.id.logout).visibility = View.GONE
        }
    }

    /**
     * 协议条款 点击事件监听
     * @author Zhenqing He
     * @createDate 2021/4/20 13:59
     */
    private fun setPolicyListener() {
        root.findViewById<ConstraintLayout>(R.id.refund_policy)
            .setOnClickListener(object : AuthCheckClickListener() {
                override fun onNoDoubleClick(v: View?) {
                    context?.let { it -> PolicyActivity.actionStart(it, "refund") }
                }
            })
        root.findViewById<ConstraintLayout>(R.id.privacy_policy)
            .setOnClickListener(object : AuthCheckClickListener() {
                override fun onNoDoubleClick(v: View?) {
                    context?.let { it -> PolicyActivity.actionStart(it, "privacy") }
                }
            })
        root.findViewById<ConstraintLayout>(R.id.term_conditions)
            .setOnClickListener(object : AuthCheckClickListener() {
                override fun onNoDoubleClick(v: View?) {
                    context?.let { it -> PolicyActivity.actionStart(it, "terms") }
                }
            })
        root.findViewById<ConstraintLayout>(R.id.intellectual)
            .setOnClickListener(object : AuthCheckClickListener() {
                override fun onNoDoubleClick(v: View?) {
                    context?.let { it -> PolicyActivity.actionStart(it, "intellectual") }
                }
            })
    }

    /**
     * 修改货币 事件监听
     * @author Zhenqing He
     * @createDate 2021/4/20 13:59
     */
    private fun setChangeCurrencyListener() {
//        root.findViewById<TextView>(R.id.currency).text =
//            MyApplication.getBaseConfig()?.currencyCode
//        root.findViewById<ConstraintLayout>(R.id.change_currency).setOnClickListener {
//            if (checkUserAuth()) {
//                context?.let { it -> ChangeCurrencyActivity.actionStart(it) }
//            } else {
//                FacebookLoginActivity.actionStart(this.requireContext())
//            }
//        }
        root.findViewById<ConstraintLayout>(R.id.setting).visibility = View.GONE
    }

    /**
     * 订单状态 点击事件监听
     * @author Zhenqing He
     * @createDate 2021/4/20 13:59
     */
    private fun setOrderProcessListener() {
        root.findViewById<LinearLayout>(R.id.order_all).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                OrderProcessActivity.actionStart(root.context, "ALL")
            }
        })
    }

    /**
     * 用户服务点击事件监听
     * @author Zhenqing He
     * @createDate 2021/4/20 14:00
     */
    private fun setUserServerListener() {
        // 地址点击监听
        root.findViewById<LinearLayout>(R.id.account_address).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                AddressActivity.actionStart(requireContext(), "list")
            }
        })
        // 客服点击监听
        root.findViewById<LinearLayout>(R.id.account_customer_service).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                SupportActivity.actionStart(requireContext())
            }
        })
        // 帮助事件点击监听
        root.findViewById<LinearLayout>(R.id.account_help).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                HelpActivity.actionStart(requireContext())
            }
        })
        // 反馈事件点击监听
        root.findViewById<LinearLayout>(R.id.account_feedback).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                FeedBackActivity.actionStart(requireContext())
            }
        })
    }

    /**
     * 用户是否 facebook 登录检查
     * @return Boolean
     * @author Zhenqing He
     * @createDate 2021/4/20 14:00
     */
    private fun checkUserAuth(): Boolean {
        Log.d("checkUserAuth", userAuth.isFacebookLogin().toString())
        return userAuth.isFacebookLogin()
    }

    /**
     * 收藏列表点击监听
     * @author Zhenqing He
     * @createDate 2021/4/20 14:06
     */
    private fun setWishListListener() {
        root.findViewById<ConstraintLayout>(R.id.account_wish_list).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                context?.let { it1 -> WishListActivity.actionStart(it1) }
            }
        })
    }

    /**
     * null
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/21 14:51
     */
    private fun setPointListener() {
        root.findViewById<ConstraintLayout>(R.id.account_points).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                context?.let { it1 -> PointActivity.actionStart(it1) }
            }
        })
    }

    /**
     * null
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/21 14:51
     */
    private fun setWalletListener() {
        root.findViewById<ConstraintLayout>(R.id.account_wallet).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                context?.let { it1 -> WalletActivity.actionStart(it1) }
            }
        })
    }

    /**
     * 优惠券点击监听
     * @author Zhenqing He
     * @createDate 2021/4/21 14:52
     */
    private fun setCouponListener() {
        root.findViewById<ConstraintLayout>(R.id.account_coupon).setOnClickListener(object : AuthCheckClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val intent = Intent(MyApplication.getInstance(), CouponActivity::class.java)
                (this@AccountFragment).activity?.startActivityForResult(intent, 2)
            }
        })
    }
}