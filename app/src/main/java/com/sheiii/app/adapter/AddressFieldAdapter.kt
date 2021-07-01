package com.sheiii.app.adapter

import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sheiii.app.R
import com.sheiii.app.model.AddressFieldModel
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.AddressViewModel
import java.util.*

/**
 * @author created by Zhenqing He on  11:28
 * @description 地址输入 adapter
 */
class AddressFieldAdapter(list: List<AddressFieldModel>, vm: AddressViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mList = list
    private val vm = vm
    private lateinit var clickInterface: ClickInterface
    private val FIELD_TYPE_TEXT = 0
    private val FIELD_TYPE_PICKER = 1

    class AddressFieldTextHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textInputLayout =
            view.findViewById<TextInputLayout>(R.id.address_textInputLayout)
        val textInputEditText =
            view.findViewById<TextInputEditText>(R.id.address_textInputEdit)
        val prefix = view.findViewById<TextView>(R.id.address_input_prefix)
    }

    class AddressFieldPickerHolder(view: View) : RecyclerView.ViewHolder(view) {
        val picker = view.findViewById<ConstraintLayout>(R.id.address_picker)
        val pickerTitle = view.findViewById<TextView>(R.id.address_picker_title)
        val pickerData = view.findViewById<TextView>(R.id.address_picker_data)
        val pickerTips = view.findViewById<TextView>(R.id.address_picker_tips)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            AddressFieldTextHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_address_field_item, parent, false)
            )
        } else {
            AddressFieldPickerHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_address_picker_field_item, parent, false)
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = mList[position]
        // 初始化输入
        vm.addressInput.value?.set(data.fieldName, data.inputValue)

        /**
         * @if operateType 类型为输入时
         * @else operateType 类型为选择时
         */
        if (holder is AddressFieldTextHolder) {

            holder.textInputLayout.hint = data.placeholder

            holder.textInputEditText.setText(data.inputValue)

            // text 输入激活监听
            holder.textInputEditText.setOnFocusChangeListener { _, hasFocus ->
                // 控制手机号码输入,只能输入数字
                if (data.fieldName == "mobile") {
                    holder.textInputEditText.inputType = InputType.TYPE_CLASS_PHONE
                }
                if (hasFocus) {
                    holder.textInputLayout.hint = data.fieldShowName
                } else {

                }
            }

            holder.textInputEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 输入验证标记 true 为通过输入验证
                    var isInputCorrect = true
                    // 输入验证
                    for ((i, validator) in data.validatorConfigs.withIndex()) {
                        if (validator.name == "required" && holder.textInputEditText.text.isNullOrEmpty()) { // 空值判断
                            holder.textInputLayout.hint = data.placeholder
                            holder.textInputLayout.isErrorEnabled = true
                            holder.textInputLayout.error = validator.message

                            isInputCorrect = false
                        } else if (validator.name == "pattern" && holder.textInputEditText.text?.let {
                                Regex(pattern = validator.pattern).containsMatchIn(
                                    input = it
                                )
                            } == false) { // 正则验证判断
                            holder.textInputLayout.isErrorEnabled = true
                            holder.textInputLayout.error = validator.message

                            isInputCorrect = false
                        } else if (validator.name == "length"
                            && (holder.textInputEditText.text?.length!! > validator.maxLength
                                    || holder.textInputEditText.text?.length!! < validator.minLength)
                        ) { // 输入长度判断
                            holder.textInputLayout.isErrorEnabled = true
                            holder.textInputLayout.error = validator.message

                            isInputCorrect = false
                        }
                    }

                    if (isInputCorrect) {
                        holder.textInputLayout.isErrorEnabled = false
                        vm.addressInput.value?.set(
                            data.fieldName,
                            holder.textInputEditText.text.toString()
                        )
                        data.inputValue = holder.textInputEditText.text.toString()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}

            })

            // 手机号前置区号判断
            if (!data.mobileCode.isNullOrEmpty()) {
                holder.prefix.text = data.mobileCode
                holder.prefix.visibility = View.VISIBLE
            }

        } else {
            holder as AddressFieldPickerHolder
            val pickerTitle = holder.pickerTitle
            val pickerData = holder.pickerData
            val pickerTips = holder.pickerTips

            pickerTitle.text = data.fieldShowName

            // 非英文站，控制无法选择区域和国家
            if (MyApplication.getBaseConfig()?.siteCode != "EN" && data.fieldName == "country") {
                pickerData.text = data.inputValue
                pickerData.visibility = View.VISIBLE
            } else {
                holder.picker.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        clickInterface.countryPickerClick(v!!)
                    }
                })
            }

            // 地区联级选择，上一个地区没有选择完，不允许点击
            if (vm.addressInput.value?.get(data.preCode).isNullOrEmpty()) {
                // TODO
            } else {
                holder.picker.setOnClickListener(object : PerfectClickListener() {
                    override fun onNoDoubleClick(v: View?) {
                        vm.country.value = vm.addressInput.value?.get("country")!!
                        vm.getAreaList(data.fieldName)
                        if (v != null) {
                            clickInterface.cityPickerClick(v, position, data.fieldName)
                        }
                    }
                })
            }

            // 如果有输入值，则直接显示
            if (data.inputValue != "") {
                pickerData.text = data.inputValue
                pickerData.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int = mList.size

    override fun getItemViewType(position: Int): Int {
        return if (mList[position].operateType == "text") FIELD_TYPE_TEXT else FIELD_TYPE_PICKER
    }

    interface ClickInterface {
        fun cityPickerClick(view: View, position: Int, type: String)

        fun countryPickerClick(view: View)
    }

    fun cityPickerClick(clickInterface: ClickInterface) {
        this.clickInterface = clickInterface
    }

    fun countryPickerClick(clickInterface: ClickInterface) {
        this.clickInterface = clickInterface
    }

}