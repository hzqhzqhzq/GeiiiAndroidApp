package com.sheiii.app.adapter

import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.model.Activity
import com.sheiii.app.model.Block
import com.sheiii.app.model.Item
import com.sheiii.app.model.Section
import com.sheiii.app.viewmodel.CartViewModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.view.activity.ActivityActivity
import com.sheiii.app.viewmodel.ProductDetailsViewModel
import org.w3c.dom.Text
import kotlin.coroutines.coroutineContext

/**
 * @author created by Zhenqing He on  16:31
 * @description
 */
class CartListAdapter(
    cartViewModel: CartViewModel,
    productDetailsViewModel: ProductDetailsViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_TYPE_TIPS = 0
    private val ITEM_TYPE_SECTION = -1
    private val ITEM_TYPE_BLOCK = -2
    private val ITEM_TYPE_PRODUCT = -3
    private val ITEM_TYPE_ACTIVITY = -4

    private val cartViewModel = cartViewModel
    private val productDetailsViewModel = productDetailsViewModel
    private val vm = cartViewModel.cart.value
    private val productList: MutableList<Any> = initData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_TIPS -> {
                TipsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_cart_tips_item, parent, false)
                )
            }
            ITEM_TYPE_SECTION -> {
                SectionViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_cart_header_item, parent, false)
                )
            }
            ITEM_TYPE_ACTIVITY -> {
                ActivityViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_cart_block_item, parent, false)
                )
            }
            ITEM_TYPE_PRODUCT -> {
                ItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_cart_product_item, parent, false)
                )
            }
            else -> {
                BlockViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_cart_activity_item, parent, false)
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var data = productList[position]
        when (holder) {
            is TipsViewHolder -> {
                data as String
                data = data.replace("<strong>", "<strong><font color=\"#e30057\">")
                data = data.replace("</strong>", "</font></strong>")
                holder.tips.text = Html.fromHtml(data, 1)
            }
            is SectionViewHolder -> {
                data as Section
                holder.tips.text = data.sectionName
                holder.cartCheck.isChecked = data.allSelected!!
                holder.cartCheck.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        cartViewModel.selectedGroup(
                            if (holder.cartCheck.isChecked) 1 else 0,
                            data.sectionType
                        )
                    }
                })
            }
            is ItemViewHolder -> {
                data as Item

                holder.productTitle.text = data.itemName
                holder.productSku.text = data.skuNames
                holder.saleprice.text = data.salePriceTxt
                holder.maketprice.text = data.marketPriceTxt
                holder.checkProduct.isChecked = data.selected == 1
                holder.buyNumber.setText(data.buyNum.toString())
                holder.image
                /* 更改Sku点击监听 */
                holder.sku.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        MyApplication.getLoading().showLoading()
                        /* 选中购物车产品时，向skuDialog中的值 */
                        cartViewModel.id.value = data.id
                        cartViewModel.skuValues.value = data.skuValues
                        cartViewModel.itemSkuId.value = data.itemSkuId
                        productDetailsViewModel.getProductDetails(data.itemSid)
                    }
                })
                /* 购买数量产品 + - 点击监听 */
                holder.addBtn?.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        modifyQuantity(0, data.buyNum, data.id)
                    }
                })
                holder.subBtn?.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        modifyQuantity(1, data.buyNum, data.id)
                    }
                })
                /* 删除事件监听 */
                holder.delete?.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        cartViewModel.deleteProduct(data.id)

                    }
                })
                /* 选择事件监听 */
                holder.checkProduct?.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        cartViewModel.checkProduct(
                            if (data.selected == 1) 0 else 1,
                            data.id
                        )
                    }
                })
                Glide.with(holder.image)
                    .load(MyApplication.getImageHost() + data.skuImage)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                    .into(holder.image)
            }
            is ActivityViewHolder -> {
                data as Activity
                if (data.promoName == "") {
                    val root = holder.itemView.findViewById<ConstraintLayout>(R.id.cart_block)
                    root.clearAnimation()
//                    val root = holder.itemView.findViewById<ConstraintLayout>(R.id.cart_block)
                    root.visibility = View.GONE
                } else {
                    holder.itemView.visibility = View.VISIBLE
                    holder.promName.text = data.promoName
                    holder.promDesc.text = data.promoDesc
                    holder.more.setOnClickListener(object : PerfectClickListener() {
                        override fun onNoDoubleClick(v: View?) {
                            ActivityActivity.actionStart(MyApplication.getInstance())
                        }
                    })
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return if (vm != null) {
            productList.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ITEM_TYPE_TIPS
            }
            else -> {
                when (productList[position]) {
                    is Section -> ITEM_TYPE_SECTION
                    is Block -> ITEM_TYPE_BLOCK
                    is Activity -> {
                        if ((productList[position] as Activity).promoName != "") ITEM_TYPE_ACTIVITY else -100
                    }
                    else -> ITEM_TYPE_PRODUCT
                }
            }
        }
    }

    class TipsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tips: TextView = view.findViewById(R.id.cart_tips_msg1)
    }

    class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tips: TextView = view.findViewById(R.id.cart_tips_msg2)
        val cartCheck: CheckBox = view.findViewById(R.id.cart_check)
    }

    class BlockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val promName = view.findViewById<TextView>(R.id.prom_name)
        val promDesc = view.findViewById<TextView>(R.id.prom_desc)
        val more = view.findViewById<TextView>(R.id.block_more)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productTitle = view.findViewById<TextView>(R.id.cart_product_title)
        val productSku = view.findViewById<TextView>(R.id.cart_product_sku)
        val saleprice = view.findViewById<TextView>(R.id.cart_product_saleprice)
        val maketprice = view.findViewById<TextView>(R.id.cart_product_maketprice)
        val checkProduct = view.findViewById<CheckBox>(R.id.check_product)
        val buyNumber = view.findViewById<EditText>(R.id.buy_number)
        val image = view.findViewById<ImageView>(R.id.cart_product_img)
        val sku = view.findViewById<LinearLayout>(R.id.cart_sku)

        val addBtn = view.findViewById<ImageView>(R.id.add_btn)
        val subBtn = view.findViewById<ImageView>(R.id.sub_btn)
        val delete = view.findViewById<ImageView>(R.id.cart_delete)
    }

    class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val promName = view.findViewById<TextView>(R.id.prom_name)
        val promDesc = view.findViewById<TextView>(R.id.prom_desc)
        val more = view.findViewById<TextView>(R.id.block_more)
    }

    /**
     * 修改购买数量
     * @param type 0:增加数量，1:减少数量
     * @param currentNum 当前购买数量
     * @param id 购买产品的sid
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/21 17:20
     */
    private fun modifyQuantity(type: Int, currentNum: Int, id: String) {
        when (type) {
            0 -> {
                cartViewModel.updateNumber(currentNum + 1, id)
//                MainActivity.mainCartIconView.updateNumber(currentNum + 1)
            }
            1 -> {
                cartViewModel.updateNumber(currentNum - 1, id)
//                MainActivity.mainCartIconView.updateNumber(currentNum - 1)
            }
        }
    }

    /**
     * 初始化adapter数据
     * @return MutableList<Any>
     * @author Zhenqing He
     * @createDate 2021/4/21 17:17
     */
    private fun initData(): MutableList<Any> {
        val resultList: MutableList<Any> = mutableListOf()
        if (vm != null) {
            resultList.add(vm.tipsMsg as Any)

            /* 此处用到四个for来遍历购物车数据，是为了分类展示，主要是用来避免recyclerview的多层嵌套 */
            for (section in vm.sectionList) {
                resultList.add(section as Any)
                for (block in section.blockList) {
                    resultList.add(block as Any)
                    for (activity in block.activityList) {
                        resultList.add(activity as Any)
                        for (item in activity.itemList) {
                            resultList.add(item as Any)
                        }
                    }
                }
            }
        }
        return resultList
    }

}