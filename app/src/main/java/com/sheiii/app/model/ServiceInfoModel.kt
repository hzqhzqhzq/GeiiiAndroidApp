package com.sheiii.app.model

data class ServiceInfoModel(
    val titleList: List<Title>
)

data class Title(
    val detailList: List<String>,
    val title: String,
    val titleIcon: String
)