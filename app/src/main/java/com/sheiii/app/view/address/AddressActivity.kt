package com.sheiii.app.view.address

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.sheiii.app.R
import com.sheiii.app.viewmodel.AddressViewModel
import com.sheiii.app.viewmodel.CheckViewModel
import com.sheiii.app.viewmodel.VMScope
import com.sheiii.app.viewmodel.injectViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState

class AddressActivity : AppCompatActivity(), AddressListFragment.AddressListCallBack {
    private lateinit var addressViewModel: AddressViewModel
//    @VMScope("check")
    private lateinit var checkViewModel: CheckViewModel
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

//    private lateinit var multiStateContainer: MultiStateContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        checkViewModel = ViewModelProvider(this).get(CheckViewModel::class.java)
        val type = intent.getStringExtra("type")

//        multiStateContainer = MultiStatePage.bindMultiState(this, object : OnRetryEventListener {
//            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
//                if (type == "edit") {
//                    fragmentTransaction.add(R.id.address_fragment, AddressEditFragment()).commit()
//                } else {
//                    fragmentTransaction.add(R.id.address_fragment, AddressListFragment()).commit()
//                }
//            }
//        })
//        multiStateContainer.show<LoadingState> { }

        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()

        if (type == "edit") {
            fragmentTransaction.add(R.id.address_fragment, AddressEditFragment()).commit()
        } else {
            fragmentTransaction.add(R.id.address_fragment, AddressListFragment()).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        addressViewModel.saveAddressState.observe(this, Observer {
            if (addressViewModel.saveAddressState.value!!) {
                checkViewModel.getOrderInfo()
                onBackPressed()
//                multiStateContainer.show<SuccessState> {  }
            } else {

            }
        })
    }

    /**
     * 打开地址详情
     * @author Zhenqing He
     * @createDate 2021/6/10 17:27
     */
    override fun openAddressDetails() {
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.address_fragment, AddressEditFragment()).commit()
    }

    /**
     * 打开新建地址
     * @author Zhenqing He
     * @createDate 2021/6/10 17:27
     */
    override fun openNewAddress() {
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.address_fragment, AddressEditFragment()).commit()
    }

    /* type 为 edit 的时候，启动 AddressEditFragment; type 为 list 的时候，启动AddressListFragment */
    companion object {
        fun actionStart(context: Context, type: String) {
            val intent = Intent(context, AddressActivity::class.java)
            intent.putExtra("type", type)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}