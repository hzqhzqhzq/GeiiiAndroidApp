package com.sheiii.app.view.account

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.BaseConfigModel
import com.sheiii.app.model.CurrencyModel
import com.sheiii.app.model.ProductModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.view.productdetails.ProductDetailsActivity
import com.sheiii.app.viewmodel.CurrencyViewModel

class ChangeCurrencyActivity : AppCompatActivity() {
    private lateinit var currencyViewModel: CurrencyViewModel
    private lateinit var currencyRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_currency)

        currencyViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)
        currencyRecyclerView = findViewById(R.id.currency_recyclerview)

        currencyViewModel.getCurrencyList()
    }

    override fun onResume() {
        super.onResume()

        currencyViewModel.currencyList.observe(this, Observer {
            if (currencyViewModel.currencyList.value != null) {
                // 初始化货币列表
                initCurrencyList()
            }
        })

        currencyViewModel.changeCurrencyResult.observe(this, Observer {
            if (currencyViewModel.changeCurrencyResult.value != null) {
                if (currencyViewModel.changeCurrencyResult.value!!) {
                    MyApplication.getLoading().hideLoading()
                    finish()
                }
            }
        })
    }

    private fun initCurrencyList() {
        currencyRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = CommonAdapter.Builder().setDatas(currencyViewModel.currencyList.value!! as List<*>)
                .setLayoutId(R.layout.recycler_currency_item)
                .bindView(object : CommonAdapter.BindView {
                    override fun onBindView(
                        viewHolder: CommonAdapter.MyViewHolder,
                        data: Any?,
                        position: Int
                    ) {
                        data as CurrencyModel
                        val image = viewHolder.itemView.findViewById<ImageView>(R.id.currency_image)
                        val check = viewHolder.itemView.findViewById<CheckBox>(R.id.currency_check)

                        viewHolder.itemView.findViewById<TextView>(R.id.currency_name).text = data.currencyCode

                        Glide.with(image).load(data.image).into(image)
                        check.isChecked = data.selected

                        // 设置currency选择监听
                        check.setOnClickListener(object : PerfectClickListener() {
                            override fun onNoDoubleClick(v: View?) {
                                if (!data.selected) {
                                    MyApplication.getLoading().showLoading()
                                    currencyViewModel.changeCurrency(data.currencyCode)
                                }
                            }
                        })
                    }
                }).create()
        }
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, ChangeCurrencyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}