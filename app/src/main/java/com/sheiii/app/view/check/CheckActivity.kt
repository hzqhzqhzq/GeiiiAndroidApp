package com.sheiii.app.view.check

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.sheiii.app.BaseActivity
import com.sheiii.app.GlideApp
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.adapter.AddressFieldAdapter
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.AddressFieldModel
import com.sheiii.app.model.CartBlockItem
import com.sheiii.app.model.CheckInfoModel
import com.sheiii.app.model.Payment
import com.sheiii.app.ui.CommonLoadingDialog
import com.sheiii.app.ui.multistatepage.MyLoadingState
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.view.address.AddressActivity
import com.sheiii.app.viewmodel.AddressViewModel
import com.sheiii.app.viewmodel.CheckViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState
import kotlin.reflect.full.memberProperties

class CheckActivity : BaseActivity() {
    private lateinit var addAddress: ConstraintLayout
    private lateinit var messageSize: TextView
    private lateinit var message: EditText

    //    @VMScope("check")
    private lateinit var checkViewModel: CheckViewModel
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var productListRecyclerView: RecyclerView
    private lateinit var productListAdapter: CommonAdapter
    private lateinit var productDetailsListRecyclerView: RecyclerView
    private lateinit var productDetailsListAdapter: CommonAdapter
    private lateinit var couponBottomSheetDialog: BottomSheetDialog
    private lateinit var payPalBottomSheetDialog: BottomSheetDialog
    private lateinit var addressRecyclerView: RecyclerView
    private val activeField: MutableList<AddressFieldModel> = mutableListOf()

    private var userRemark = ""
    private var addressId = ""

    override fun init() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setLoading(this)

//        addAddress = findViewById(R.id.add_address)
        addressRecyclerView = findViewById(R.id.recycler_address_field)
        messageSize = findViewById(R.id.message_size)
        message = findViewById(R.id.message_input)

        checkViewModel = ViewModelProvider(this).get(CheckViewModel::class.java)
        checkViewModel.getOrderInfo()
        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        addressViewModel.getAddressFieldList(null)
        couponBottomSheetDialog = BottomSheetDialog(this)
        couponBottomSheetDialog.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_checkout_coupon,
                null
            )
        )

        payPalBottomSheetDialog = BottomSheetDialog(this)
        payPalBottomSheetDialog.setContentView(layoutInflater.inflate(R.layout.dialog_paypal, null))
        payPalBottomSheetDialog.setCancelable(false)
        payPalBottomSheetDialog.setCanceledOnTouchOutside(true)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_check
    }

    override fun setToolBar() { }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        checkViewModel.checkInfo.observe(this, Observer {
                // 初始化产品列表
                initProductList()
                // 初始化支付方式
                initPayMethod()
                // 初始化优惠券
                initCoupon()
                // 初始化支付信息
                initPayMessage()
                // 初始化购买产品详情
                initProductDetails()

                initAddress()

                // 用户订单备注输入监听
                userRemarkInput()
                // 下单
                checkoutOrder()
                // 初始化优惠券弹窗
                if (it.couponList != null) {
                    initCouponDialog()
                }
                // 初始化 paypal支付dialog
                initPayPalWebView()

                multiStateContainer.show<SuccessState> { }
        })

        checkViewModel.orderId.observe(this, Observer {
                // 处理下单的结果
                handleOrder()
        })

        checkViewModel.cartNumber.observe(this, Observer {
            MyApplication.setCartNumber(it)
        })
    }

    /**
     * 初始化下单产品列表
     * @author Zhenqing He
     * @createDate 2021/4/28 13:14
     */
    private fun initProductList() {
        val list = if (checkViewModel.checkInfo.value!!.cartBlock.itemList.size <= 4)
            checkViewModel.checkInfo.value!!.cartBlock.itemList
        else checkViewModel.checkInfo.value!!.cartBlock.itemList.subList(0, 4)
        findViewById<TextView>(R.id.check_product_num).text =
            checkViewModel.checkInfo.value!!.cartBlock.itemList.size.toString()

        productListAdapter = CommonAdapter.Builder()
            .setDatas(list as List<*>)
            .setLayoutId(R.layout.recycler_check_product_item)
            .bindView(object : CommonAdapter.BindView {
                override fun onBindView(
                    viewHolder: CommonAdapter.MyViewHolder,
                    data: Any?,
                    position: Int
                ) {
                    val image =
                        viewHolder.itemView.findViewById<ImageView>(R.id.check_product_image)
                    data as CartBlockItem
                    Glide.with(image)
                        .load(MyApplication.getImageHost() + data.skuImage)
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(5)))
                        .into(image)
                }
            })
            .create()
        productListRecyclerView = findViewById<RecyclerView>(R.id.product_list_recycler).apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = StaggeredGridLayoutManager(
                6,
                StaggeredGridLayoutManager.VERTICAL
            )
            adapter = productListAdapter
        }

        // 控制产品详情列表显示
        findViewById<ConstraintLayout>(R.id.product_num).setOnClickListener(object :
            PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val details = findViewById<RecyclerView>(R.id.check_puduct_list_details)
                val simple = findViewById<RecyclerView>(R.id.product_list_recycler)
                val change = findViewById<ImageView>(R.id.check_product_change)
                if (details.visibility == View.VISIBLE) {
                    details.visibility = View.GONE
                    simple.visibility = View.VISIBLE
                    change.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_expand_more_24))
                } else {
                    details.visibility = View.VISIBLE
                    simple.visibility = View.GONE
                    change.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24))
                }
            }
        })
    }

    /**
     * 初始化支付方式
     * @author Zhenqing He
     * @createDate 2021/4/29 17:47
     */
    private fun initPayMethod() {
        findViewById<RecyclerView>(R.id.paymeny_method_recycler).apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter =
                CommonAdapter.Builder().setDatas(checkViewModel.checkInfo.value?.paymentList!!)
                    .setLayoutId(R.layout.recycler_check_pay_method)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            data as Payment
                            val view = viewHolder.itemView
                            val check = view.findViewById<CheckBox>(R.id.payment_method_check)
                            val image = view.findViewById<ImageView>(R.id.payment_method_image)
                            val name = view.findViewById<TextView>(R.id.payment_method_text)
                            name.text = data.paymentName
                            GlideApp.with(image).load(MyApplication.getImageHost() + data.iconUrl)
                                .into(image)
                            if (position == 0) {
                                check.isChecked = true
                                checkViewModel.selectPayment(data.paymentId)
                            }
                            check.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    for (p in checkViewModel.checkInfo.value?.paymentList!!.indices) {
                                        if (p != position) {
                                            findViewById<RecyclerView>(R.id.paymeny_method_recycler).getChildAt(
                                                p
                                            )
                                                .findViewById<CheckBox>(R.id.payment_method_check).isChecked =
                                                false
                                        } else {
                                            check.isChecked = true
                                            checkViewModel.selectPayment(data.paymentId)
                                        }
                                    }
                                }
                            }
                        }
                    }).create()
        }
    }

    /**
     * 初始化优惠券
     * @author Zhenqing He
     * @createDate 2021/4/29 17:47
     */
    private fun initCoupon() {
        findViewById<TextView>(R.id.coupon_tips).text = checkViewModel.checkInfo.value?.couponTips
        val noUseCoupon = findViewById<RadioButton>(R.id.no_use_coupon)
        val useCoupon = findViewById<RadioButton>(R.id.use_coupon)
        noUseCoupon.isChecked = true
        if (checkViewModel.checkInfo.value?.couponList == null) {
            useCoupon.isEnabled = false
        } else {
            useCoupon.isEnabled = true
            useCoupon.setOnClickListener(object : PerfectClickListener() {
                override fun onNoDoubleClick(v: View?) {
                    if (useCoupon.isChecked) {
                        couponBottomSheetDialog.show()
                    }
                }
            })
        }
    }

    /**
     * 初始化支付信息
     * @author Zhenqing He
     * @createDate 2021/4/29 17:48
     */
    private fun initPayMessage() {
        findViewById<TextView>(R.id.subtotal).text =
            checkViewModel.checkInfo.value?.cartBlock?.totalSalePriceTxt
        findViewById<TextView>(R.id.freight).text =
            checkViewModel.checkInfo.value?.cartBlock?.totalShippingPriceTxt
        findViewById<TextView>(R.id.total).text =
            checkViewModel.checkInfo.value?.cartBlock?.totalPayPriceTxt
        findViewById<TextView>(R.id.order_total).text =
            "${resources.getString(R.string.total)}:${checkViewModel.checkInfo.value?.cartBlock?.totalPayPriceTxt}"
    }

    /**
     * 初始化购买产品列表详情
     * @author Zhenqing He
     * @createDate 2021/4/28 13:15
     */
    private fun initProductDetails() {
        productDetailsListAdapter = CommonAdapter.Builder()
            .setDatas(checkViewModel.checkInfo.value!!.cartBlock.itemList)
            .setLayoutId(R.layout.recycler_check_product_details_item)
            .bindView(object : CommonAdapter.BindView {
                override fun onBindView(
                    viewHolder: CommonAdapter.MyViewHolder,
                    data: Any?,
                    position: Int
                ) {
                    data as CartBlockItem
                    val image =
                        viewHolder.itemView.findViewById<ImageView>(R.id.check_product_details_image)
                    Glide.with(image)
                        .load(MyApplication.getImageHost() + data.skuImage)
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(5)))
                        .into(image)
                    viewHolder.itemView.findViewById<TextView>(R.id.check_product_details_title).text =
                        data.itemName
                    viewHolder.itemView.findViewById<TextView>(R.id.check_product_details_sku).text =
                        data.skuNames
                    viewHolder.itemView.findViewById<TextView>(R.id.check_product_details_salePrice).text =
                        data.salePriceTxt
                    viewHolder.itemView.findViewById<TextView>(R.id.check_product_details_marketPrice).text =
                        data.marketPriceTxt
                    viewHolder.itemView.findViewById<TextView>(R.id.check_product_details_buynum).text =
                        "x ${data.buyNum}"
                }
            })
            .create()
        productDetailsListRecyclerView =
            findViewById<RecyclerView>(R.id.check_puduct_list_details).apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(
                    1,
                    StaggeredGridLayoutManager.VERTICAL
                )
                adapter = productDetailsListAdapter
            }
    }

    /**
     * 初始化下单页面订单弹窗
     * @author Zhenqing He
     * @createDate 2021/4/30 10:03
     */
    private fun initCouponDialog() {
        val couponRecyclerView =
            couponBottomSheetDialog.findViewById<RecyclerView>(R.id.checkout_coupon_recyclerview)
//        couponRecyclerView?.apply {
//            setHasFixedSize(false)
//            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
//            adapter = CommonAdapter.Builder().setLayoutId(R.layout.recycler_coupon_unused).setDatas().bindView(object : CommonAdapter.BindView {
//                override fun onBindView(
//                    viewHolder: CommonAdapter.MyViewHolder,
//                    data: Any?,
//                    position: Int
//                ) {
//
//                }
//            }).create()
//        }
    }

    /**
     * 初始化地址
     * @author Zhenqing He
     * @createDate 2021/4/29 17:49
     */
    private fun initAddress() {
        addressViewModel.addressFieldList.observe(this, Observer { it ->
            /* 后端返回数据，可用的字段 */
            for ((i, field) in it.withIndex()) {
                if (field.inputEnable) {
                    activeField.add(field)
                }
            }

            if (checkViewModel.checkInfo.value?.address != null) {
                val data = checkViewModel.checkInfo.value?.address
                addressId = data?.id!!

                Log.d("check-error", checkViewModel.checkInfo.value.toString())

                data::class.memberProperties.forEach { tt ->
                    for (field in activeField) {
                        if (tt.name == field.fieldName) {
                            field.inputValue = tt.getter.call(data) as String
                        }
                    }
                }
            }

            addressRecyclerView.apply {
                isNestedScrollingEnabled = false
                layoutManager = LinearLayoutManager(context)
                adapter = AddressFieldAdapter(activeField, addressViewModel)
            }
        })
    }

    /**
     * null
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/6/22 14:29
     */
    private fun saveAddress() {
        // 用来判断输入是否全部正确
        var isInputCorrect = true

        // 验证提交数据的结果
        for ((i, field) in activeField.withIndex()) {
            val child = addressRecyclerView.layoutManager!!.findViewByPosition(i)
            /**
             * @if operateType 为 text 则是输入验证
             * @else 为 select 则是选择验证
             */
            if (field.operateType == "text") {
                val textInputLayout =
                    child?.findViewById<TextInputLayout>(R.id.address_textInputLayout)
                for (validator in field.validatorConfigs) {
                    // 如果有非空要求，则进行非空检查
                    if (addressViewModel.addressInput.value?.get(field.fieldName)
                            .isNullOrEmpty()
                    ) {
                        if (validator.name == "required") {
                            textInputLayout?.isErrorEnabled = true
                            textInputLayout?.error = validator.message
                            isInputCorrect = false
                        }
                    } else {
                        // 正则检查
                        if (validator.name == "pattern" && addressViewModel.addressInput.value?.get(
                                field.fieldName
                            )?.let {
                                Regex(pattern = validator.pattern).containsMatchIn(
                                    input = it
                                )
                            } == false
                        ) {
                            textInputLayout?.isErrorEnabled = true
                            textInputLayout?.error = validator.message
                            isInputCorrect = false
                        }
                        // 输入长度检查
                        if (validator.name == "length" && (addressViewModel.addressInput.value?.get(
                                field.fieldName
                            )?.length!! > validator.maxLength
                                    || addressViewModel.addressInput.value?.get(field.fieldName)?.length!! < validator.minLength)
                        ) {
                            textInputLayout?.isErrorEnabled = true
                            textInputLayout?.error = validator.message
                            isInputCorrect = false
                        }
                    }
                }
            } else if (field.operateType == "select") {
                val pickerTips = child?.findViewById<TextView>(R.id.address_picker_tips)
                for (validator in field.validatorConfigs) {
                    // 非空判断
                    if (validator.name == "required" && addressViewModel.addressInput.value?.get(
                            field.fieldName
                        ).isNullOrEmpty()
                    ) {
                        pickerTips?.text = validator.message
                        pickerTips?.setTextColor(resources.getColor(R.color.red))
                        pickerTips?.visibility = View.VISIBLE
                        isInputCorrect = false
                    } else {
                        pickerTips?.visibility = View.GONE
                    }
                }
            }
        }

        if (isInputCorrect) {
            addressViewModel.saveAddress(addressId)
        } else {
            Log.d("isInputCorrect", "error")
        }
    }
//    private fun initAddress() {
//        val addressData = checkViewModel.checkInfo.value?.address
//        val showAddress = findViewById<ConstraintLayout>(R.id.show_address)
//        val addAddress = findViewById<ConstraintLayout>(R.id.add_address)
//        if (addressData != null) {
//            showAddress.visibility = View.VISIBLE
//            addAddress.visibility = View.GONE
//
//            findViewById<TextView>(R.id.consignee).text = addressData.userName
//            findViewById<TextView>(R.id.show_address_phone).text = addressData.mobile
//            Log.d("show_address_1", addressData.addressTxt)
//            findViewById<TextView>(R.id.show_address_1).text =
//                addressData.addressTxt.replace(";", " ")
//
//            showAddress.setOnClickListener(object : PerfectClickListener() {
//                override fun onNoDoubleClick(v: View?) {
//                    AddressActivity.actionStart(this@CheckActivity, "list")
//                }
//            })
//
//        } else {
//            addAddress.visibility = View.VISIBLE
//            showAddress.visibility = View.GONE
//
//            addAddress.setOnClickListener(object : PerfectClickListener() {
//                override fun onNoDoubleClick(v: View?) {
//                    AddressActivity.actionStart(this@CheckActivity, "edit")
//                }
//            })
//        }
//    }

    private fun userRemarkInput() {
        message.addTextChangedListener(object : TextWatcher {
            private val text = messageSize

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userRemark = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                var num: Int = s!!.length
                num = 300 - num
                text.text = "$num/300"
            }
        })
    }

    private fun checkoutOrder() {
        val orderBtn = findViewById<TextView>(R.id.check_place_order)
        orderBtn.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                loading.showLoading()
                saveAddress()
//            payPalBottomSheetDialog.show()
            }
        })

        addressViewModel.saveAddressState.observe(this, Observer {
            if (it != null && it) {
                checkViewModel.order(
                    checkViewModel.checkInfo.value?.address?.id!!,
                    userRemark,
                    checkViewModel.checkInfo.value?.usePaymentId!!
                )
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initPayPalWebView() {
        val uri = "https://www.baidu.com"
        val webView = payPalBottomSheetDialog.findViewById<WebView>(R.id.paypal_webview)
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(uri)
                return true
            }
        }
        webView?.loadUrl(uri)
    }

    private fun handleOrder() {
        checkViewModel.getCartNumber()
        OrderSuccessActivity.actionStart(this, checkViewModel.orderId.value!!)
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, CheckActivity::class.java)
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