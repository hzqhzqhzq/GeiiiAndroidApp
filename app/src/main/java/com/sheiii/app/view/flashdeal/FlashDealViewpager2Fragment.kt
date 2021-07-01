package com.sheiii.app.view.flashdeal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.daimajia.numberprogressbar.NumberProgressBar
import com.sheiii.app.GlideApp
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.FlashDealProduct
import com.sheiii.app.util.MyApplication
import com.sheiii.app.viewmodel.FlashDealViewModel

/**
 * @author created by Zhenqing He on  09:26
 * @description
 */
class FlashDealViewpager2Fragment : Fragment() {
    private val flashDealViewModel by activityViewModels<FlashDealViewModel>()
    private lateinit var root: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var end: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_flashdeal_viewpager, container, false)
        recyclerView = root.findViewById(R.id.flash_deal_recyclerview)
        end = root.findViewById(R.id.flash_deal_end)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val flashPage = flashDealViewModel.flashPage.value

        if (flashPage?.itemData?.list?.isNotEmpty()!!) {
            recyclerView.visibility = View.VISIBLE
            recyclerView.apply {
                setHasFixedSize(false)
                layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                adapter = CommonAdapter.Builder()
                    .setDatas(flashDealViewModel.flashPage.value?.itemData?.list!!)
                    .setLayoutId(R.layout.recycler_flash_deal_product)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            data as FlashDealProduct
                            val view = viewHolder.itemView
                            view.findViewById<TextView>(R.id.flash_deal_product_name).text =
                                data.title
                            view.findViewById<TextView>(R.id.flash_deal_product_saleprice).text =
                                data.salePriceTxt
                            view.findViewById<TextView>(R.id.flash_deal_product_marketprice).text =
                                data.marketPriceTxt
                            view.findViewById<TextView>(R.id.flash_deal_product_stock).text =
                                "${resources.getString(R.string.sold)} ${data.totalStock.toString()}"
                            val image = view.findViewById<ImageView>(R.id.flash_deal_product_image)
                            view.findViewById<NumberProgressBar>(R.id.number_progress_bar).progress =
                                data.soldNum / data.totalStock

                            GlideApp.with(image).load(MyApplication.getImageHost() + data.image)
                                .placeholder(R.drawable.bg_logo_jpg_405x540).into(image)
                        }
                    }).create()
            }

            end.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            end.visibility = View.VISIBLE
        }
    }
}