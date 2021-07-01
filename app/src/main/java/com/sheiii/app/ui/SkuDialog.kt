package com.sheiii.app.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.Value
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.ProductDetailsViewModel
import java.util.*

/**
 * sku选择器弹窗
 * @param productDetailsViewModel
 * @param context
 * @param lifecycleOwner
 * @param type 1表示从购物车进来 0表示从其他页面进入
 * @param checkedSku 当从购物车页面进入的时候传入当前选中的sku prop， 否则传 ""
 * @param itemSkuId 当从购物车页面进入的时候传入当前选中的sku id， 否则传 0
 * @author Zhenqing He
 * @createDate 2021/4/20 18:03
 */
class SkuDialog(
    productDetailsViewModel: ProductDetailsViewModel,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    type: Int,
    checkedSku: String,
    itemSkuId: Int
) {
    private val SKU_SIZE_RECYCLERVIEW_SIZE = 4
    private val SKU_COLOR_RECYCLERVIEW_SIZE = 3

    private lateinit var skuDialog: BottomSheetDialog
    private val productDetailsViewModel = productDetailsViewModel
    private val context = context
    private val sid = productDetailsViewModel.thisSid.value
    private val lifecycleOwner = lifecycleOwner
    private val type = type
    private var checkSkuList = mutableListOf<String>()
    private var checkSkuPropPath = ""
    private lateinit var skuListColorAdapter: CommonAdapter
    private lateinit var skuListSizeAdapter: CommonAdapter
    private lateinit var skuColorRecyclerView: RecyclerView
    private lateinit var skuSizeRecyclerView: RecyclerView
    private var skuId = itemSkuId
    private var buyNumber = 1
    private val checkedSku = checkedSku

    private lateinit var skuMsg: TextView
    private var skuMsgText = ""

    /* sku 初始化 */
    fun initSkuDialog(callback: SkuCallback) {
        initSkuData()
        skuDialog = BottomSheetDialog(context, R.style.TransparentDialog)
        val skuData = productDetailsViewModel.itemBase.value?.skuPropList
        skuDialog.setContentView(R.layout.dialog_sku)

        val skuMainImg = skuDialog.findViewById<ImageView>(R.id.sku_main_img)

        Glide.with(skuMainImg!!)
            .load(MyApplication.getImageHost() + productDetailsViewModel.itemBase.value?.image)
            .placeholder(R.drawable.test).into(skuMainImg)

        skuDialog.findViewById<TextView>(R.id.cart_product_saleprice)?.text =
            productDetailsViewModel.itemBase.value?.salePriceTxt
        skuDialog.findViewById<TextView>(R.id.market_price)?.text =
            productDetailsViewModel.itemBase.value?.marketPriceTxt

        skuMsg = skuDialog.findViewById(R.id.sku_msg)!!

        /* 显示 sku 颜色 尺码等 */
        for ((index, item) in skuData!!.withIndex()) {
            val pid = item.pid
            /* Color Sku */
            if (item.isSizeAttr == 0) {
                val title = skuDialog.findViewById<TextView>(R.id.title_1)
                title?.text = item.name
                title?.visibility = View.VISIBLE
                val skuColorAdapter = CommonAdapter.Builder()
                    .setDatas(item.values as List<*>)
                    .setLayoutId(R.layout.recycler_sku_image_item)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            val image = viewHolder.itemView.findViewById<ImageView>(R.id.image)
                            data as Value
                            Glide.with(image)
                                .load(MyApplication.getImageHost() + data.image)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                                .into(image)
                            viewHolder.itemView.findViewById<TextView>(R.id.image_name).text =
                                data.name

                            val pvid = pid.toString() + ':' + data.vid.toString()

                            /* 初始化的时候 sku 判断是否可用 */
                            if (!data.disable) {
                                viewHolder.itemView.setOnClickListener(object : PerfectClickListener() {
                                    override fun onNoDoubleClick(v: View?) {
                                        clickSku(pvid, index, position)
                                        Glide.with(skuMainImg!!)
                                            .load(MyApplication.getImageHost() + data.image)
                                            .placeholder(R.drawable.test).into(skuMainImg)
                                        updateView(data, viewHolder, item.isSizeAttr)
                                        generateCheckSkuPropPath()
                                        if (checkSkuList.size == productDetailsViewModel.itemBase.value?.skuPropList?.size) {
                                            checkQuantity(null)
                                        } else {
                                            checkSkuPropPath = ""
                                            skuId = -1
                                        }
                                    }
                                })
//                                Glide.with(skuMainImg!!)
//                                    .load(MyApplication.getImageHost() + data.image)
//                                    .placeholder(R.drawable.test).into(skuMainImg)
                                updateView(data, viewHolder, item.isSizeAttr)

                                /* 如果初始化的时候，当前选中sku结果集未空，则默认选中第一个 */
                                if (checkSkuList.isNullOrEmpty()) {
                                    viewHolder.itemView.callOnClick()
                                }
                            } else {
                                viewHolder.itemView.background =
                                    context.resources.getDrawable(R.drawable.background_sku_color_disable)
                            }
                        }
                    })
                    .create()
                skuColorRecyclerView =
                    skuDialog.findViewById<RecyclerView>(R.id.sku_img_recycler)?.apply {
                        setHasFixedSize(true)
                        layoutManager = StaggeredGridLayoutManager(
                            SKU_COLOR_RECYCLERVIEW_SIZE,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        adapter = skuColorAdapter
                    }!!
            } else { // size sku
                val title = skuDialog.findViewById<TextView>(R.id.title_2)
                title?.text = item.name
                title?.visibility = View.VISIBLE
                val skuSizeAdapter = CommonAdapter.Builder()
                    .setDatas(item.values as List<*>)
                    .setLayoutId(R.layout.recycler_sku_size_item)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            val size = viewHolder.itemView.findViewById<TextView>(R.id.size)
                            data as Value
                            size.text = data.name

                            val pvid = pid.toString() + ':' + data.vid.toString()

                            /* 初始化的时候 sku 判断是否可用 */
                            if (!data.disable) {
                                viewHolder.itemView.setOnClickListener(object : PerfectClickListener() {
                                    override fun onNoDoubleClick(v: View?) {
                                        clickSku(pvid, index, position)
                                        updateView(data, viewHolder, item.isSizeAttr)
                                        showSizeMsg(data)
                                        generateCheckSkuPropPath()
                                        if (checkSkuList.size == productDetailsViewModel.itemBase.value?.skuPropList?.size) {
                                            checkQuantity(null)
                                        } else {
                                            checkSkuPropPath = ""
                                            skuId = -1
                                        }
                                    }
                                })
                                updateView(data, viewHolder, item.isSizeAttr)
                            } else {
//                                    viewHolder.itemView.background = resources.getDrawable(R.drawable.)
                            }
                        }
                    })
                    .create()
                /* 设置 sku size 超出屏幕换行布局 */
                val manager = object : FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }

                manager.flexWrap = FlexWrap.WRAP
                manager.flexDirection = FlexDirection.ROW
                manager.justifyContent = JustifyContent.FLEX_START

                skuSizeRecyclerView =
                    skuDialog.findViewById<RecyclerView>(R.id.sku_size_recycler)?.apply {
                        setHasFixedSize(true)
                        layoutManager = manager
                        adapter = skuSizeAdapter
                    }!!
            }
        }

        /* 购买数量产品 + - 点击监听 */
        skuDialog.findViewById<ImageView>(R.id.add_btn)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                modifyQuantity(0)
            }
        })
        skuDialog.findViewById<ImageView>(R.id.sub_btn)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                modifyQuantity(1)
            }
        })

        /* 提交 sku 加入购物车 button 监听 */
        skuDialog.findViewById<TextView>(R.id.save_sku)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (type == 0) {
                    callback.addCart(sid!!, skuId, buyNumber)
                } else if (type == 1) {
                    callback.updateSku(sid!!, skuId, buyNumber)
                }
            }
        })
        skuDialog.show()
    }

    /* 初始化 sku 属性集合 用来判断首次加载的时候 sku 是否可选择
    *  checkedSku 为""时，说明为产品详情页sku
    *  */
    private fun initSkuData() {
        val result = TreeSet<String>()
        /* 将sku 最小集放入 result 中，用来初始化判断 */
        for (sku in productDetailsViewModel.itemSkuList.value!!) {
            val list = sku.propPath.split(";")
            result.addAll(list)
        }
        for ((i, p) in productDetailsViewModel.itemBase.value?.skuPropList!!.withIndex()) {
            for ((j, v) in p.values.withIndex()) {
                productDetailsViewModel.itemBase.value?.skuPropList!![i].values[j].disable =
                    !result.contains(p.pid.toString() + ":" + v.vid.toString())
            }
        }
        if (checkedSku != "") {
            val checkedSkuList = checkedSku.split(";")
            checkSkuList = checkedSkuList as MutableList<String>
            for ((i, p) in productDetailsViewModel.itemBase.value?.skuPropList!!.withIndex()) {
                for ((j, v) in p.values.withIndex()) {
                    if (checkedSkuList.contains("${p.pid}:${v.vid}")) {
                        productDetailsViewModel.itemBase.value?.skuPropList!![i].values[j].active =
                            true
                    }
                }
            }
        }
    }

    /*
    * 1. 点击特效怎么处理
    * 2. 取消点击怎么处理
    * 3. sku 组合
    * */
    fun clickSku(pvid: String, index: Int, position: Int) {
        val pid = pvid.split(":")[0]
        val vid = pvid.split(":")[1]
//        Log.d("cccclicksku", checkSkuList.contains(pvid).toString())
        if (checkSkuList.contains(pvid)) {
            productDetailsViewModel.itemBase.value?.skuPropList!![index].values[position].active =
                false
            checkSkuList.remove(pvid)
        } else {
            /* 判断 sku 是否全部选择完毕 true 为选择完毕操作 false 为未选择完毕并且继续检查其他 sku 属性是否可选 */
            var removeItem = ""
            for (item in checkSkuList) {
                Log.d("removeCheck1", (item.indexOf(pid) != -1).toString())
                Log.d("removeCheck2", (item.split(":")[0] == pid).toString())
                if (item.split(":")[0] == pid) {
                    removeItem = item
                    productDetailsViewModel.itemBase.value?.skuPropList!![index].values[position].active =
                        false
                }
            }

            if (removeItem != "") {
                checkSkuList.remove(removeItem)
            }

            checkSkuList.add(pvid)
            checkSkuList.sort()

            /* 获取选中 sku 当前的结果集 */
            var checkSkuId = checkSkuList[0]
            if (checkSkuList.size > 1) {
                for (item in checkSkuList) {
                    if (item != checkSkuId) {
                        checkSkuId = "$checkSkuId;$item"
                    }
                }
            }

            /* 从 itemSkuList 中获取当前选中 sku 结果集的超集result */
            val result = TreeSet<String>()
            for (sku in productDetailsViewModel.itemSkuList.value!!) {
                if (sku.propPath.indexOf(pvid) != -1) {
                    result.add(sku.propPath)
                }
            }

            for ((i, p) in productDetailsViewModel.itemBase.value?.skuPropList!!.withIndex()) {
                for ((j, v) in p.values.withIndex()) {
                    /* 不同pid 检查 disable */
                    if (p.pid.toString() != pid) {
                        var boolean = true
                        for (r in result) {
                            if (r.indexOf(p.pid.toString() + ":" + v.vid.toString()) != -1) {
                                boolean = false
                            }
                        }
                        productDetailsViewModel.itemBase.value?.skuPropList!![i].values[j].disable =
                            boolean
                    } else {
                        /* 相同 pid 检查 active */
                        if (v.active && v.vid.toString() != vid) {
                            productDetailsViewModel.itemBase.value?.skuPropList!![i].values[j].active =
                                false
                            if (p.isSizeAttr == 0) {
                                skuColorRecyclerView.adapter?.notifyItemChanged(j)
                            } else {
                                skuSizeRecyclerView.adapter?.notifyItemChanged(j)
                            }
                        } else if (v.vid.toString() == vid) {
                            productDetailsViewModel.itemBase.value?.skuPropList!![i].values[j].active =
                                true
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新 sku active 视图
     *
     * @param data 选中的sku size
     * @param viewHolder sku绑定的视图
     * @param isSizeAttr 判断是否是size sku
     */
    fun updateView(data: Value, viewHolder: CommonAdapter.MyViewHolder, isSizeAttr: Int) {
        if (isSizeAttr == 0) {
            if (data.active) {
                viewHolder.itemView.background =
                    context.resources.getDrawable(R.drawable.background_sku_color_active, context.theme)
            } else {
                viewHolder.itemView.background =
                    context.resources.getDrawable(R.drawable.border_eee_rd6, context.theme)
            }
        } else {
            if (data.active) {
                viewHolder.itemView.findViewById<TextView>(R.id.size).background =
                    context.resources.getDrawable(R.drawable.background_sku_color_active, context.theme)
            } else {
                viewHolder.itemView.findViewById<TextView>(R.id.size).background =
                    context.resources.getDrawable(R.drawable.border_eee_rd6, context.theme)
            }
        }
    }

    /**
     * 选择尺码时，显示尺码详情
     *
     * @param data 选中的sku size
     */
    fun showSizeMsg(data: Value) {
        val msg = skuDialog.findViewById<TextView>(R.id.size_msg)
        var msgTxt = ""
        if (data.active) {
            if (productDetailsViewModel.itemSize.value != null) {
                for (item in productDetailsViewModel.itemSize.value?.sizeTipList!!) {
                    if (item[0] == data.vid.toString()) {
                        for (msg in item.slice(1 until item.size)) {
                            msgTxt = if (msgTxt != "") {
                                msgTxt + "\n" + msg
                            } else {
                                msg
                            }
                        }
                        msg?.text = msgTxt
                        break
                    }
                }
                msg?.visibility = View.VISIBLE
            }
        } else {
            msg?.visibility = View.INVISIBLE
        }
    }

    /**
     * 检查库存数量
     * @author Zhenqing He
     * @createDate 2021/5/13 15:13
     */
    fun checkQuantity(@Nullable quantity: Int?) {
        val add = skuDialog.findViewById<ImageView>(R.id.add_btn)
        val sub = skuDialog.findViewById<ImageView>(R.id.sub_btn)

        Log.d("checkSkuPropPath111", checkSkuPropPath)

        for (sku in productDetailsViewModel.itemSkuList.value!!) {
            if (sku.quantity <= 1) {
//                sub?.setBackgroundColor(resources.getColor(R.color.quantity_disable))
//                add?.setBackgroundColor(resources.getColor(R.color.quantity_disable))
            } else if (checkSkuPropPath == sku.propPath) {
                skuId = sku.skuId

                var currentQuantity = ""
                if (quantity == null) {
                    currentQuantity = skuDialog.findViewById<EditText>(R.id.buy_number)?.text.toString()
                } else {
                    currentQuantity = quantity.toString()
                }

                Log.d("currentQuantity", currentQuantity)
                if (currentQuantity.toInt() <= 1) {
                    sub?.setBackgroundColor(context.resources.getColor(R.color.quantity_disable, context.theme))
                    sub?.setImageDrawable(context.resources.getDrawable(R.drawable.ic_baseline_horizontal_rule_24_grey, context.theme))
                    add?.setBackgroundColor(context.resources.getColor(R.color.quantity_active, context.theme))
                    add?.setImageDrawable(context.resources.getDrawable(R.drawable.ic_baseline_add_24, context.theme))
                } else if (1 < currentQuantity.toInt() && currentQuantity.toInt() < sku.quantity) {
                    sub?.setBackgroundColor(context.resources.getColor(R.color.quantity_active, context.theme))
                    sub?.setImageDrawable(context.resources.getDrawable(R.drawable.ic_baseline_horizontal_rule_24, context.theme))
                    add?.setBackgroundColor(context.resources.getColor(R.color.quantity_active, context.theme))
                    add?.setImageDrawable(context.resources.getDrawable(R.drawable.ic_baseline_add_24, context.theme))
                } else {
                    sub?.setBackgroundColor(context.resources.getColor(R.color.quantity_active, context.theme))
                    sub?.setImageDrawable(context.resources.getDrawable(R.drawable.ic_baseline_horizontal_rule_24, context.theme))
                    add?.setBackgroundColor(context.resources.getColor(R.color.quantity_disable, context.theme))
                    add?.setImageDrawable(context.resources.getDrawable(R.drawable.ic_baseline_add_24_grey, context.theme))
                }
                break
            }
        }
    }

    /**
     * 修改购买数量
     * @param type 0:增加 1:减少
     * @author Zhenqing He
     * @createDate 2021/5/13 15:12
     */
    private fun modifyQuantity(type: Int) {
        var currentQuantity: Int =
            skuDialog.findViewById<EditText>(R.id.buy_number)?.text.toString().toInt()
        if (checkSkuList.size == productDetailsViewModel.itemBase.value?.skuPropList?.size) {
            when (type) {
                0 -> {
                    for (sku in productDetailsViewModel.itemSkuList.value!!) {
                        if (checkSkuPropPath == sku.propPath) {
                            if (currentQuantity < sku.quantity) {
                                currentQuantity++
                                checkQuantity(currentQuantity)
                            }
                            break
                        }
                    }
                }
                1 -> {
                    for (sku in productDetailsViewModel.itemSkuList.value!!) {
                        if (checkSkuPropPath == sku.propPath) {
                            if (currentQuantity > 1) {
                                currentQuantity--
                                checkQuantity(currentQuantity)
                            }
                            break
                        }
                    }
                }
            }
            skuDialog.findViewById<EditText>(R.id.buy_number)?.setText(currentQuantity.toString())
            buyNumber = currentQuantity
        }
    }

    /**
     * 生成当前选中 SKU 的 propPath, 并且设置sku当前选中的tips
     */
    fun generateCheckSkuPropPath() {
        checkSkuPropPath = ""
        // 生成 propPath
        Log.d("checkSkuList", checkSkuList.toString())

        for (sku in checkSkuList) {
            checkSkuPropPath = if (checkSkuPropPath == "") {
                sku
            } else {
                "$checkSkuPropPath;$sku"
            }
        }

        // 生成选中sku tips
        skuMsgText = ""
        for(skuProp in productDetailsViewModel.itemBase.value?.skuPropList!!) {
            for (v in skuProp.values) {
                if (v.active) {
                    skuMsgText = if (skuMsgText == "") {
                        v.name
                    } else {
                        "$skuMsgText & ${v.name}"
                    }
                }
            }
        }
        skuMsg.text = if (skuMsgText == "") {
            MyApplication.getInstance().resources.getString(R.string.choice)
        } else {
            skuMsgText
        }
    }

    fun dismiss() {
        skuDialog.dismiss()
    }

    /**
     * 请求回调接口 请求成功之后，实现该接口的onSuccess方法，将数据和viewmodel绑定
     */
    interface SkuCallback {
        fun addCart(sid: String, skuId: Int, buyNumber: Int)

        fun updateSku(sid: String, skuId: Int, buyNumber: Int)
    }
}