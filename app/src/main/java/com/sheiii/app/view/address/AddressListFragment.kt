package com.sheiii.app.view.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.AddressDetails
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.AddressViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState

/**
 * @author created by Zhenqing He on  13:19
 * @description
 */
class AddressListFragment : Fragment() {
    private val addressViewModel: AddressViewModel by activityViewModels()
    private lateinit var addressListRecyclerView: RecyclerView
    private lateinit var root: View
    private lateinit var addNewAddress: TextView

    private lateinit var multiStateContainer: MultiStateContainer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_address_list, container, false)

        multiStateContainer = MultiStatePage.bindMultiState(root, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                addressViewModel.getAddressList()
            }
        })
        multiStateContainer.show<LoadingState> { }

        addressListRecyclerView = root.findViewById(R.id.address_list_recycler)
        addNewAddress = root.findViewById(R.id.address_list_add)
        addressViewModel.getAddressList()
        initAddressList()
        return multiStateContainer
    }

    private fun initAddressList() {
        addressViewModel.addressList.observe(viewLifecycleOwner, Observer {
            if (addressViewModel.addressList.value != null) {
                addressListRecyclerView.apply {
                    setHasFixedSize(false)
                    layoutManager = LinearLayoutManager(this.context)
                    adapter = CommonAdapter.Builder().setDatas(addressViewModel.addressList.value?.list as List<*>)
                        .setLayoutId(R.layout.recycler_address_list_item)
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                data as AddressDetails

                                val userName = viewHolder.itemView.findViewById<TextView>(R.id.address_username)
                                val userPhone = viewHolder.itemView.findViewById<TextView>(R.id.address_phone)
                                val userAddressDetails = viewHolder.itemView.findViewById<TextView>(R.id.address_details)
                                val addressModify = viewHolder.itemView.findViewById<ImageView>(R.id.address_modify)
                                val select = viewHolder.itemView.findViewById<CheckBox>(R.id.address_select)

                                userName.text = data.userName
                                userPhone.text = data.mobile
                                userAddressDetails.text = data.addressTxt

                                if (data.selected) {
                                    select.isChecked = true
                                }

                                // 修改地址监听
                                addressModify.setOnClickListener(object : PerfectClickListener() {
                                    override fun onNoDoubleClick(v: View?) {
                                        addressViewModel.getAddressDetails(data.id, data.country)
                                        (activity as AddressListCallBack).openAddressDetails()
                                    }
                                })

                                /* 选择地址监听 */
                                select.setOnClickListener(object : PerfectClickListener() {
                                    override fun onNoDoubleClick(v: View?) {
                                        addressViewModel.selectAddress(data.id)
                                        activity?.finish()
                                    }
                                })
                            }
                        }).create()
                }

                multiStateContainer.show<SuccessState> {  }
            }
        })

        addNewAddress.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                (activity as AddressListCallBack).openNewAddress()
            }
        })
    }

    interface AddressListCallBack {
        // 修改地址
        fun openAddressDetails()
        // 新增地址
        fun openNewAddress()
    }
}