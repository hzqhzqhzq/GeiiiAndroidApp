package com.sheiii.app.model

data class SizeChartModel(
    val itemSize: ItemSize,
    val sizeExplanation: List<SizeExplanation>
)

data class ItemSize(
    val sizeCmList: List<List<String>>,
    val sizeHeaderList: List<String>,
    val sizeInchList: List<List<String>>,
    val sizeTipList: List<Map<String, List<String>>>
)

data class SizeExplanation(
    val contentList: List<String>,
    val title: String
)