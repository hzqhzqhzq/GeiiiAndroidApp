package com.sheiii.app.view.wishlist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sheiii.app.GlideApp
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.WishProduct
import com.sheiii.app.ui.CommonLoadingDialog
import com.sheiii.app.ui.SkuDialog
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.ProductDetailsViewModel
import com.sheiii.app.viewmodel.WishListViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.EmptyState
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState

class WishListActivity : AppCompatActivity() {
    private lateinit var wishListViewModel: WishListViewModel
    private lateinit var wishListRecyclerView: RecyclerView
    private lateinit var productDetailsViewModel: ProductDetailsViewModel
    private lateinit var skuDialog: SkuDialog
    private lateinit var loading: CommonLoadingDialog
    private lateinit var moreDialog: BottomSheetDialog

    private lateinit var multiStateContainer: MultiStateContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_list)

        multiStateContainer = MultiStatePage.bindMultiState(this, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                wishListViewModel.getWishList(null)
            }
        })
        multiStateContainer.show<LoadingState> { }

        wishListViewModel = ViewModelProvider(this).get(WishListViewModel::class.java)
        productDetailsViewModel = ViewModelProvider(this).get(ProductDetailsViewModel::class.java)
        wishListViewModel.getWishList(null)

        wishListRecyclerView = findViewById(R.id.wish_list_recyclerview)

        loading = CommonLoadingDialog.buildDialog(this)

        moreDialog = BottomSheetDialog(this)
        moreDialog.setContentView(R.layout.dialog_wish_list_more)

    }

    override fun onResume() {
        super.onResume()
        wishListViewModel.wishList.observe(this, Observer {
            if (wishListViewModel.wishList.value != null) {
                initWishList()
                openSkuDialog()
                selectAllListener()
                deleteListener()
                if (wishListViewModel.wishList.value!!.size == 0) {
                    multiStateContainer.show<EmptyState> {  }
                } else {
                    multiStateContainer.show<SuccessState> {  }
                }
            }

        })
    }

    /**
     * 初始化 wish list
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/20 15:59
     */
    private fun initWishList() {
        val product = wishListViewModel.wishList.value
        wishListRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = CommonAdapter.Builder().setLayoutId(R.layout.recycler_wish_list_product)
                .setDatas(product as List<*>)
                .bindView(object : CommonAdapter.BindView {
                    override fun onBindView(
                        viewHolder: CommonAdapter.MyViewHolder,
                        data: Any?,
                        position: Int
                    ) {
                        data as WishProduct
                        val view = viewHolder.itemView
                        val image = view.findViewById<ImageView>(R.id.wish_list_product_image)
                        view.findViewById<TextView>(R.id.wish_list_product_title).text = data.title
                        view.findViewById<TextView>(R.id.wish_list_product_sale_price).text =
                            data.salePriceTxt
                        view.findViewById<TextView>(R.id.wish_list_product_market_price).text =
                            data.marketPriceTxt

                        GlideApp.with(image).load(MyApplication.getImageHost() + data.image).apply(
                            RequestOptions.bitmapTransform(RoundedCorners(50))
                        ).into(image)

                        view.findViewById<ImageView>(R.id.wish_list_product_add_cart)
                            .setOnClickListener(object : PerfectClickListener() {
                                override fun onNoDoubleClick(v: View?) {
                                    loading.showLoading()
                                    productDetailsViewModel.getProductDetails(data.sid)
                                }
                            })

                        view.findViewById<ImageView>(R.id.wish_list_product_more)
                            .setOnClickListener(object : PerfectClickListener() {
                                override fun onNoDoubleClick(v: View?) {
                                    if (moreDialog.isShowing) {
                                        moreDialog.dismiss()
                                    } else {
                                        moreDialog.show()
                                    }
                                }
                            })

                        view.findViewById<CheckBox>(R.id.wish_list_select).setOnClickListener(object : PerfectClickListener() {
                            override fun onNoDoubleClick(v: View?) {

                            }
                        })
                    }
                }).create()
        }
    }

    /**
     * 点击加入购物车，打开sku
     * @author Zhenqing He
     * @createDate 2021/4/20 17:41
     */
    private fun openSkuDialog() {
        productDetailsViewModel.itemBase.observe(this, Observer {
            if (productDetailsViewModel.itemBase.value != null) {
                skuDialog = SkuDialog(
                    productDetailsViewModel,
                    this,
                    this,
                    0,
                    "",
                    -1
                )
                skuDialog.initSkuDialog(object : SkuDialog.SkuCallback {
                    override fun addCart(sid: String, skuId: Int, buyNumber: Int) {
                        productDetailsViewModel.addCart(sid, skuId, buyNumber)
                    }

                    override fun updateSku(sid: String, skuId: Int, buyNumber: Int) {
                        TODO("Not yet implemented")
                    }
                })

                loading.hideLoading()
            }
        })

        productDetailsViewModel.addCartMessage.observe(this, Observer {
            val data = productDetailsViewModel.addCartMessage.value
            if (data != "") {
                if (data == "SUCCESS") {
                    productDetailsViewModel.addCartTips.observe(
                        this,
                        Observer {
                            if (productDetailsViewModel.addCartTips.value != "") {
                                val successDialog = AlertDialog.Builder(this)
                                    .setMessage(productDetailsViewModel.addCartTips.value!!)
                                    .setTitle("添加购物车成功！")
                                    .setPositiveButton(
                                        "去购物车",
                                        DialogInterface.OnClickListener { _, _ ->
                                            val intent = Intent()
                                            setResult(RESULT_OK, intent)
                                            skuDialog.dismiss()
                                            finish()
                                        })
                                    .setCancelable(false)
                                    .setNegativeButton(
                                        "继续购物",
                                        DialogInterface.OnClickListener { _, _ ->
                                            val intent = Intent()
                                            setResult(RESULT_CANCELED, intent)
                                            skuDialog.dismiss()
                                            finish()
                                        })
                                    .create()
                                successDialog.show()
                            }
                        })
                }
            }
        })
    }

    /**
     * 选中所有 监听
     * @author Zhenqing He
     * @createDate 2021/4/21 10:35
     */
    private fun selectAllListener() {
        val selectAll = findViewById<CheckBox>(R.id.wish_list_select_all)
        selectAll.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (selectAll.isChecked) {
                    for (index in 0 until wishListViewModel.wishList.value?.size!!) {
                        wishListRecyclerView.get(index).findViewById<CheckBox>(R.id.wish_list_select).isChecked = true
                    }
                } else {
                    for (index in 0 until wishListViewModel.wishList.value?.size!!) {
                        wishListRecyclerView.get(index).findViewById<CheckBox>(R.id.wish_list_select).isChecked = false
                    }
                }
            }
        })
    }

    /**
     * 删除所有选中商品的监听
     * @author Zhenqing He
     * @createDate 2021/4/21 11:28
     */
    private fun deleteListener() {
        findViewById<TextView>(R.id.wish_list_delete_select).setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
            }
        })
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, WishListActivity::class.java)
//            intent.putExtra("searchUrl", searchUrl)
            context.startActivity(intent)
        }
    }
}