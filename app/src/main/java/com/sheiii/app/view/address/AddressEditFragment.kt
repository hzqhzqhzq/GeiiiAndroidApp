package com.sheiii.app.view.address

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.sheiii.app.R
import com.sheiii.app.adapter.AddressFieldAdapter
import com.sheiii.app.model.AddressFieldModel
import com.sheiii.app.ui.CityPickerDialog
import com.sheiii.app.ui.CountryPickerDialog
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.AddressViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState


/**
 * @author created by Zhenqing He on  13:27
 * @description
 */
class AddressEditFragment : Fragment() {
    private val addressViewModel: AddressViewModel by activityViewModels()
    private lateinit var addressFieldRecyclerView: RecyclerView
    private lateinit var addressFieldAdapter: AddressFieldAdapter
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var cityPickerDialog: CityPickerDialog
    private lateinit var countryPickerDialog: CountryPickerDialog

    private lateinit var root: View

    private var addressId = ""

    private lateinit var multiStateContainer: MultiStateContainer

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleOwner = this

        addressViewModel.getAddressFieldList(null)
        addressViewModel.getCountryList()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_address_edit, container, false)

        multiStateContainer = MultiStatePage.bindMultiState(root, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                addressViewModel.getAddressFieldList(null)
                addressViewModel.getCountryList()
            }
        })
        multiStateContainer.show<LoadingState> { }

        addressFieldRecyclerView = root.findViewById(R.id.address_field)

        return multiStateContainer
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        addressViewModel.addressFieldList.observe(this, Observer {
            if (!addressViewModel.addressFieldList.value.isNullOrEmpty()) {
                cityPickerDialog = CityPickerDialog(addressViewModel)
                countryPickerDialog = CountryPickerDialog(addressViewModel)
                initAddressField()
                multiStateContainer.show<SuccessState> {  }
            }
        })

        // ?????? arealist ????????????????????????dialog
        addressViewModel.areaList.observe(lifecycleOwner, Observer {
            if (addressViewModel.areaList.value != null) {
                if (cityPickerDialog.dialog == null || !cityPickerDialog.dialog?.isShowing!!) {
                    if (!cityPickerDialog.isAdded) {
                        cityPickerDialog.show(requireFragmentManager(), null)
                    }
                } else if (cityPickerDialog.dialog != null && cityPickerDialog.dialog!!.isShowing) {
                    cityPickerDialog.addAreaList()
                } else {
                    if (!cityPickerDialog.isAdded) {
                        cityPickerDialog.show(requireFragmentManager(), null)
                    }
                }
            }
        })

        // cityPicker ????????????????????????????????????????????????cityPicker?????????
        addressViewModel.cityPickerCallBack.observe(lifecycleOwner, Observer {
            if (addressViewModel.cityPickerCallBack.value!!) {
                for ((index, field) in addressViewModel.addressFieldList.value!!.withIndex()) {
                    if (field.operateType == "select" && field.inputValue.isNotEmpty()) {
                        addressFieldAdapter.notifyItemChanged(index)
                    }
                }

                cityPickerDialog.dialog?.dismiss()
                countryPickerDialog.dialog?.dismiss()
//                addressFieldAdapter.notifyDataSetChanged()
            }
        })

        // ?????? addressdetails ???????????????????????????????????????????????????????????????
        addressViewModel.addressDetails.observe(lifecycleOwner, Observer {
            if (addressViewModel.addressDetails.value != null) {
                val addressDetailsMap = addressViewModel.addressDetails.value?.serializeToMap()
                for (field in addressViewModel.addressFieldList.value!!) {
                    field.inputValue = addressDetailsMap?.get(field.fieldName) as String
                }
                addressId = addressViewModel.addressDetails.value?.id!!
                addressFieldRecyclerView.adapter?.notifyDataSetChanged()
            }
        })

    }

    /**
     * ??????????????????input field??????????????????????????????disabled???field
     * @author Zhenqing He
     * @createDate 2021/4/22 10:26
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun initAddressField() {
        /* ???????????????????????????????????? */
        val activeField: MutableList<AddressFieldModel> = mutableListOf()
        for ((i, field) in addressViewModel.addressFieldList.value?.withIndex()!!) {
            if (field.inputEnable) {
                activeField.add(field)
            }
        }

        addressFieldAdapter = AddressFieldAdapter(activeField, addressViewModel)
        addressFieldRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL
            )
            adapter = addressFieldAdapter
        }

        // ????????????????????????
        root.findViewById<TextView>(R.id.address_save).setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                // ????????????????????????????????????
                var isInputCorrect = true

                // ???????????????????????????
                for ((i, field) in activeField.withIndex()) {
                    val child = addressFieldRecyclerView.layoutManager!!.findViewByPosition(i)
                    /**
                     * @if operateType ??? text ??????????????????
                     * @else ??? select ??????????????????
                     */
                    if (field.operateType == "text") {
                        val textInputLayout =
                            child?.findViewById<TextInputLayout>(R.id.address_textInputLayout)
                        for (validator in field.validatorConfigs) {
                            // ?????????????????????????????????????????????
                            if (addressViewModel.addressInput.value?.get(field.fieldName)
                                    .isNullOrEmpty()
                            ) {
                                if (validator.name == "required") {
                                    textInputLayout?.isErrorEnabled = true
                                    textInputLayout?.error = validator.message
                                    isInputCorrect = false
                                }
                            } else {
                                // ????????????
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
                                // ??????????????????
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
                            // ????????????
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
        })

        // ?????? adapter ????????????????????????????????????????????????
        addressFieldAdapter.cityPickerClick(object : AddressFieldAdapter.ClickInterface {
            override fun cityPickerClick(view: View, position: Int, type: String) {
                addressViewModel.cityPickerCallBack.value = false
            }
            override fun countryPickerClick(view: View) {}
        })
        /* ?????? ?????????????????? ??????dialog */
        addressFieldAdapter.countryPickerClick(object : AddressFieldAdapter.ClickInterface {
            override fun cityPickerClick(view: View, position: Int, type: String) {}

            override fun countryPickerClick(view: View) {
                countryPickerDialog.show(requireFragmentManager(), null)
            }
        })
    }

    override fun onStop() {
        super.onStop()
//        addressViewModel.areaList.value = null
        cityPickerDialog.dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
//        addressViewModel.areaList.value = null
        cityPickerDialog.dialog?.dismiss()
    }
}