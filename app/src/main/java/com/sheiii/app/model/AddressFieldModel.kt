package com.sheiii.app.model

data class AddressFieldModel(
    val fieldName: String,
    val fieldShowName: String,
    val id: Int,
    val inputActive: Boolean,
    val inputEnable: Boolean,
    val inputType: String,
    var inputValue: String,
    val nextCode: String,
    val operateType: String,
    val placeholder: String,
    val preCode: String,
    val mobileCode: String,
    val validatorConfigs: List<ValidatorConfig>
)

data class ValidatorConfig(
    val message: String,
    val name: String,
    val maxLength: Int,
    val minLength: Int,
    val pattern: String
)