package com.sheiii.app.view.cart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.adapter.CartListAdapter
import com.sheiii.app.ui.CommonLoadingDialog
import com.sheiii.app.view.login.LoginActivity
import com.sheiii.app.util.MyApplication
import com.sheiii.app.ui.SkuDialog
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.util.UserAuth
import com.sheiii.app.view.check.CheckActivity
import com.sheiii.app.viewmodel.CartViewModel
import com.sheiii.app.viewmodel.ProductDetailsViewModel

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartListAdapter
    private lateinit var productDetailsViewModel: ProductDetailsViewModel

    /* loading */
    private lateinit var cartLoading: ViewStub

    /* 购物车为空提示 */
    private lateinit var cartEmpty: ViewStub

    /* 底部下单的固定栏 */
    private lateinit var cartOrderLayout: ConstraintLayout

    /* 购物车为空时候的购物按钮 */
    private lateinit var emptyGoShopping: Button
    private lateinit var checkAll: CheckBox
    private lateinit var totalPay: TextView
    private lateinit var placeOrder: TextView
    private val userAuth: UserAuth = UserAuth()

    private lateinit var root: View


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        productDetailsViewModel = ViewModelProvider(this).get(ProductDetailsViewModel::class.java)

//        setLoading(requireContext())

        root = inflater.inflate(R.layout.fragment_cart, container, false)

        checkAll = root.findViewById(R.id.check_all)
        totalPay = root.findViewById(R.id.total_pay)

        cartLoading = root.findViewById(R.id.cart_loading)
        cartLoading.visibility = View.VISIBLE

        cartEmpty = root.findViewById(R.id.cart_empty)
        cartEmpty.inflate()

        cartOrderLayout = root.findViewById((R.id.cart_order_layout))
        cartOrderLayout.visibility = View.GONE

        cartViewModel.getCartList()
        /* 购物车recyclerview 初始化 */
        cartRecyclerView = root.findViewById(R.id.cart_recycler)

        placeOrder = root.findViewById(R.id.check_place_order)
        placeOrder.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                MyApplication.getLoading().showLoading()
                if (userAuth.isLogin()) {
                    CheckActivity.actionStart(requireContext())
                } else {
                    LoginActivity.actionStart(this@CartFragment.requireContext(), "checkout")
                }
            }
        })
        return root
    }

    override fun onResume() {
        super.onResume()

        val lifecycleOwner = this
        val myContext = context
        lateinit var sku: SkuDialog

        cartViewModel.cart.observe(lifecycleOwner, Observer {
            if (cartViewModel.cart.value != null) {
                cartAdapter = CartListAdapter(cartViewModel, productDetailsViewModel)
                cartRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter = cartAdapter
                }

                totalPay.text = cartViewModel.cart.value!!.totalPayPriceTxt
                checkAll.isChecked = cartViewModel.cart.value!!.allSelected

                cartLoading.visibility = View.INVISIBLE
                cartEmpty.visibility = View.GONE
                cartOrderLayout.visibility = View.VISIBLE
                MyApplication.getLoading().hideLoading()
            } else {
                cartLoading.visibility = View.INVISIBLE
                cartEmpty.visibility = View.VISIBLE
                cartOrderLayout.visibility = View.GONE
                MyApplication.getLoading().hideLoading()

                emptyGoShopping = root.findViewById(R.id.go_shopping)
                emptyGoShopping.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        val mainActivity = activity as MainActivity
                        mainActivity.changeNavView(0)
                    }
                })
            }
        })

        /* 点击购物车产品sku的时候打开skuDialog */
        productDetailsViewModel.itemBase.observe(lifecycleOwner, Observer {
            if (productDetailsViewModel.itemBase.value != null) {
                sku = SkuDialog(
                    productDetailsViewModel,
                    myContext!!,
                    this,
                    1,
                    cartViewModel.skuValues.value!!,
                    cartViewModel.itemSkuId.value!!
                )
                sku.initSkuDialog(object : SkuDialog.SkuCallback {
                    override fun addCart(sid: String, skuId: Int, buyNumber: Int) {
                        TODO("Not yet implemented")
                    }

                    /* 执行点击保存之后的回调 */
                    override fun updateSku(sid: String, skuId: Int, buyNumber: Int) {
                        cartViewModel.updateSku(sid, skuId, buyNumber)
                    }
                })
                MyApplication.getLoading().hideLoading()
            }
        })

        cartViewModel.updateSku.observe(lifecycleOwner, Observer {
            if (cartViewModel.updateSku.value == "ok") {
                sku.dismiss()
            } else {
                Toast.makeText(
                    myContext,
                    cartViewModel.updateSku.value,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}