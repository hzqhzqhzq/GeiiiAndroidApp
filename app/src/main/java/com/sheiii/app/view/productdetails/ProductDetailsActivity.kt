package com.sheiii.app.view.productdetails

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.iwgang.countdownview.CountdownView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.sheiii.app.BaseActivity
import com.sheiii.app.GlideApp
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.adapter.*
import com.sheiii.app.databinding.ActivityProductDetailsBinding
import com.sheiii.app.model.*
import com.sheiii.app.ui.CommonLoadingDialog
import com.sheiii.app.ui.IconNumberView
import com.sheiii.app.ui.MyViewPagerr
import com.sheiii.app.view.productdetails.reviewsdetails.ReviewsDetailsActivity
import com.sheiii.app.view.productdetails.sizechart.SizeChartActivity
import com.sheiii.app.ui.SkuDialog
import com.sheiii.app.ui.multistatepage.MyLoadingState
import com.sheiii.app.util.*
import com.sheiii.app.viewmodel.ProductDetailsViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState
import me.samlss.broccoli.Broccoli
import me.samlss.broccoli.BroccoliGradientDrawable
import me.samlss.broccoli.PlaceholderParameter


class ProductDetailsActivity : BaseActivity() {
    private lateinit var productDetailsViewModel: ProductDetailsViewModel
    private lateinit var descTextRecyclerView: RecyclerView
    private lateinit var promotionTipsRecyclerView: RecyclerView
    private lateinit var alsoLikeRecyclerView: RecyclerView
    private lateinit var sid: String
    private lateinit var salePromotionDialog: BottomSheetDialog
    private lateinit var serviceDetailsDialog: BottomSheetDialog
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var myContext: Context

    /* 猜你喜欢模块滚动加载标志 */
    private var alsoLikeLoadingFlag = true

    /* 存储当前显示的 产品描述图片 */
    private val alsoLikeList = mutableListOf<MutableList<ProductModel>>()
    private lateinit var productDetailsRecyclerView: RecyclerView
    private lateinit var productDetailsAdapter: ProductDetailsAdapter
    private val initProductDetailsList = mutableListOf<Any>()
    private lateinit var refreshLayout: RefreshLayout
    private lateinit var scrollListener: AppBarLayout.OnOffsetChangedListener

    override fun init() {
        multiStateContainer.show<MyLoadingState> { }

        refreshLayout = findViewById<View>(R.id.refreshLayout) as RefreshLayout
//        refreshLayout.setEnableNestedScroll(false)
        refreshLayout.setEnableRefresh(false)
        refreshLayout.setRefreshFooter(ClassicsFooter(this))
        refreshLayout.setOnLoadMoreListener { refreshlayout ->
//            refreshlayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
            productDetailsViewModel.getRecommendList(
                productDetailsViewModel.itemBase.value?.categoryId!!,
                sid
            )
        }

        setLoading(this)

        productDetailsRecyclerView = findViewById(R.id.product_details_recyclerview)

        /* init viewmodel and get product details by volley */
        sid = intent.getStringExtra("sid").toString()
        productDetailsViewModel = ViewModelProvider(this).get(ProductDetailsViewModel::class.java)
        productDetailsViewModel.getProductDetails(sid)

        /* 优惠信息 */
        salePromotionDialog = BottomSheetDialog(this)
        salePromotionDialog.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_product_details_points,
                null
            )
        )

        /* 服务详情 */
        serviceDetailsDialog = BottomSheetDialog(this)
        serviceDetailsDialog.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_product_details_service,
                null
            )
        )

        alsoLikeRecyclerView = findViewById(R.id.product_details_also_like_recyclerview)

        lifecycleOwner = this

        productDetailsViewModel.productDetails.observe(this, Observer {
            if (productDetailsViewModel.productDetails.value != null) {
                val details = productDetailsViewModel.productDetails.value
                /* 初始化 productDetails 数据集 */
                initProductDetailsList.add(details?.itemBase!!)
                initProductDetailsList.add(details.serviceList)
                if (details.itemReviews != null) {
                    initProductDetailsList.add(details.itemReviews)
                } else {
                    initProductDetailsList.add("null")
                }

                /* 初始化 recyclerview */
                productDetailsRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter = ProductDetailsAdapter(
                        initProductDetailsList,
                        productDetailsViewModel,
                        context,
                        sid
                    )
                }

                openServiceDetailsDialog()

                initDescImage()
                initDescText()

                multiStateContainer.show<SuccessState> { }
            }
        })

        initAlsoLikeRecommend()

        findViewById<ConstraintLayout>(R.id.product_controll_add).setOnClickListener(object :
            PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                openSkuDialog()
            }
        })

        productDetailsViewModel.cartNumber.observe(this, Observer {
            MyApplication.setCartNumber(it)
        })

        /* 返回首页监听 */
        findViewById<ImageView>(R.id.product_controll_home).setOnClickListener {
            MainActivity.actionStart(this)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_details_test
    }

    override fun setToolBar() { }

    /**
     * 产品描述图片滚动加载
     * @author Zhenqing He
     * @createDate 2021/6/16 10:33
     */
    private fun initDescImage() {
        var index = 0
        val data = productDetailsViewModel.productDetails.value?.itemContent
        val layout = findViewById<LinearLayout>(R.id.desc_image_test)

        val appBarLayout = findViewById<AppBarLayout>(R.id.product_details_appbarlayout)
        scrollListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if ((layout.height + resources.displayMetrics.heightPixels - 800) < -verticalOffset) {
                if (index < data?.size!! - 1) {
                    val image = ImageView(this)
                    layout.addView(image)
                    GlideApp.with(image).load(data[index]).placeholder(R.drawable.bg_logo_320x320)
                        .into(image)
                    index++
                } else {
                    appBarLayout.removeOnOffsetChangedListener(scrollListener)
                    findViewById<ImageView>(R.id.desc_default_image).visibility = View.GONE
                    /* 获取产品底部猜你喜欢 */
                    productDetailsViewModel.getRecommendList(
                        productDetailsViewModel.productDetails.value!!.itemBase.categoryId,
                        sid
                    )
                }
            }
        }
        appBarLayout?.addOnOffsetChangedListener(scrollListener)

    }

    /**
     * 初始化产品描述文字
     * @author Zhenqing He
     * @createDate 2021/6/17 11:39
     */
    private fun initDescText() {
        val data = productDetailsViewModel.productDetails.value?.itemBase?.itemPropList
        descTextRecyclerView = findViewById(R.id.description_content_recyclerview)
        descTextRecyclerView.apply {
            isNestedScrollingEnabled = false
            layoutManager =
                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = CommonAdapter.Builder().setDatas(data!!)
                .setLayoutId(R.layout.recycler_product_content_desc)
                .bindView(object : CommonAdapter.BindView {
                    override fun onBindView(
                        viewHolder: CommonAdapter.MyViewHolder,
                        data: Any?,
                        position: Int
                    ) {
                        data as ItemProp
                        viewHolder.itemView.findViewById<TextView>(R.id.prop_value).text =
                            data.propValue
                        viewHolder.itemView.findViewById<TextView>(R.id.prop_name).text =
                            data.propName
                    }
                }).create()
        }
    }

    /**
     * 初始化产品优惠信息
     * @author Zhenqing He
     * @createDate 2021/5/11 11:07
     */
    private fun initPromotionTips() {
        productDetailsViewModel.tipsMsgList.observe(this, Observer {
            if (productDetailsViewModel.tipsMsgList.value != null) {
                promotionTipsRecyclerView.apply {
                    setHasFixedSize(false)
                    layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter = CommonAdapter.Builder()
                        .setDatas(productDetailsViewModel.tipsMsgList.value!!)
                        .setLayoutId(R.layout.recycler_product_details_tips)
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                data as TipsMsg
                                val view = viewHolder.itemView
                                view.findViewById<TextView>(R.id.message).text = data.titleDesc
                                view.setOnClickListener(object : PerfectClickListener() {
                                    override fun onNoDoubleClick(v: View?) {

                                    }
                                })
                            }
                        }).create()
                }
            }
        })

    }

    /**
     * 打开sku弹窗
     * @author Zhenqing He
     * @createDate 2021/6/24 12:03
     */
    private fun openSkuDialog() {
        val sku = SkuDialog(productDetailsViewModel, this, this, 0, "", -1)
        sku.initSkuDialog(object : SkuDialog.SkuCallback {
            override fun addCart(sid: String, skuId: Int, buyNumber: Int) {
                loading.showLoading()
                Log.d("ssskuid", skuId.toString())
                productDetailsViewModel.addCart(sid, skuId, buyNumber)
                productDetailsViewModel.addCartMessage.observe(lifecycleOwner, Observer {
                    if (productDetailsViewModel.addCartMessage.value != "") {
                        loading.hideLoading()
                        if (productDetailsViewModel.addCartMessage.value == "SUCCESS") {
                            productDetailsViewModel.addCartTips.observe(
                                lifecycleOwner,
                                Observer {
                                    if (productDetailsViewModel.addCartTips.value != "") {
//                                        val successDialog = AlertDialog.Builder(myContext)
//                                            .setMessage(productDetailsViewModel.addCartTips.value!!)
//                                            .setTitle("添加购物车成功！")
//                                            .setPositiveButton(
//                                                "去购物车",
//                                                DialogInterface.OnClickListener { _, _ ->
//                                                    val intent = Intent()
//                                                    setResult(RESULT_OK, intent)
//                                                    sku.dismiss()
//                                                    finish()
//                                                })
//                                            .setCancelable(false)
//                                            .setNegativeButton(
//                                                "继续购物",
//                                                DialogInterface.OnClickListener { _, _ ->
//                                                    val intent = Intent()
//                                                    setResult(RESULT_CANCELED, intent)
//                                                    sku.dismiss()
//                                                    finish()
//                                                })
//                                            .create()
//                                        successDialog.show()
                                        baseConfigViewModel.getCartNumber()
                                        val intent = Intent()
                                        setResult(RESULT_OK, intent)
                                        sku.dismiss()
                                        finish()
                                    }
                                })
                        } else {
                            Toast.makeText(
                                applicationContext,
                                productDetailsViewModel.addCartMessage.value,
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("1111111111", productDetailsViewModel.addCartMessage.value!!)
                        }
                    }
                })
            }

            override fun updateSku(sid: String, skuId: Int, buyNumber: Int) {
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * 打开服务详情弹窗
     */
    private fun openServiceDetailsDialog() {
        val serviceDetailsRecyclerView =
            serviceDetailsDialog.findViewById<RecyclerView>(R.id.service_details_recyclerview)
        productDetailsViewModel.serviceDetails.observe(this, Observer {
            if (productDetailsViewModel.serviceDetails.value != null) {
                if (serviceDetailsRecyclerView?.adapter == null) {
                    serviceDetailsRecyclerView?.apply {
                        setHasFixedSize(false)
                        layoutManager =
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        adapter = CommonAdapter.Builder()
                            .setDatas(productDetailsViewModel.serviceDetails.value!!)
                            .setLayoutId(R.layout.recycler_product_details_service_details)
                            .bindView(object : CommonAdapter.BindView {
                                override fun onBindView(
                                    viewHolder: CommonAdapter.MyViewHolder,
                                    data: Any?,
                                    position: Int
                                ) {
                                    data as ServiceDetails

                                    val title =
                                        viewHolder.itemView.findViewById<TextView>(R.id.service_details_title)
                                    val content =
                                        viewHolder.itemView.findViewById<WebView>(R.id.service_details_content)
                                    var contentData = ""

                                    title.text = data.title

                                    for (item in data.detailList) {
                                        contentData = "${contentData}${item}"
                                    }

                                    /* 将service details 内容放到 webview中 */
                                    content.webViewClient = WebViewClient()
                                    content.loadDataWithBaseURL(
                                        null,
                                        "<html><body>${contentData}</body><html>",
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                }
                            }).create()
                    }
                    serviceDetailsDialog.show()
                } else {
                    serviceDetailsDialog.show()
                }
                ProductDetailsActivity.getLoading().hideLoading()
            }
        })
    }

    /**
     * 初始化活动、优惠内容
     * @author Zhenqing He
     * @createDate 2021/5/21 10:02
     */
//    private fun initPromotion() {
//        productDetailsViewModel.promotion.observe(this, Observer {
//            if (productDetailsViewModel.promotion.value != null) {
//                findViewById<ConstraintLayout>(R.id.product_details_flash).visibility = View.VISIBLE
//                var title = ""
//                for (item in productDetailsViewModel.promotion.value!!.promoTipList) {
//                    title = if (title == "") {
//                        item
//                    } else {
//                        "${title}\n${item}"
//                    }
//                }
//                findViewById<TextView>(R.id.promotion_title).text = title
//                findViewById<CountdownView>(R.id.flashdeal_countdown).start((productDetailsViewModel.promotion.value!!.leftSeconds * 1000).toLong())
//            }
//        })
//    }

    /**
     * 初始化猜你喜欢模块
     * @author Zhenqing He
     * @createDate 2021/5/12 11:18
     */
    private fun initAlsoLikeRecommend() {

        productDetailsViewModel.alsoLikeRecommendList.observe(this, Observer {
            if (productDetailsViewModel.alsoLikeRecommendList.value != null) {
                /* 记录之前alsolikeList 的大小 */
                val oldSize = alsoLikeList.size
                alsoLikeList.clear()
                alsoLikeList.addAll(InitProductListTwo().initList(productDetailsViewModel.alsoLikeRecommendList.value!!.list))
                /**
                 * @if adapter 不为空则直接更新adapter
                 * @else adapter 为空则recyclerview初始化
                 */
                if (alsoLikeRecyclerView.adapter != null) {
                    alsoLikeRecyclerView.adapter?.notifyItemRangeInserted(
                        oldSize,
                        (alsoLikeList.size - oldSize)
                    )
                    alsoLikeLoadingFlag = true
                } else {
                    alsoLikeRecyclerView.apply {
//                        isNestedScrollingEnabled = false
                        setHasFixedSize(false)
                        layoutManager =
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        adapter = ProductListTwoAdapter(
                            alsoLikeList,
                            null
                        )
                    }
                    alsoLikeLoadingFlag = true
                }
                refreshLayout.finishLoadMore()

                // 判断分页是否是最后一页，如果是，则关闭loading监听
                if (productDetailsViewModel.alsoLikeRecommendList.value!!.lastPage) {
                    Log.d("closeHeaderOrFooter", "1111111")
                    refreshLayout.setEnableLoadMore(false)
                }
            }
        })

    }

    companion object {
        fun actionStart(context: Context, sid: String) {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("sid", sid)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        private lateinit var loading: CommonLoadingDialog
        fun getLoading(): CommonLoadingDialog {
            return loading
        }

        fun setLoading(context: Context) {
            this.loading = MyApplication.getLoadingByActivity(context)
        }
    }
}