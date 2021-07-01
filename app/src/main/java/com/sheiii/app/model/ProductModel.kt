package com.sheiii.app.model

data class ProductListModel(
    var firstPage: Boolean,
    var lastPage: Boolean,
    val list: MutableList<ProductModel>,
    var pageNumber: Int,
    val pageSize: Int,
    val totalPage: Int,
    val totalRow: Int
)

data class ProductDetailsModel(
    val itemBase: ProductDetailsItemBaseModel,
    val itemContent: List<String>,
    val itemReviews: ProductDetailsItemReviews,
    val itemSkuList: List<ProductDetailsItemSkuListItem>,
    val publishStatus: Int,
    val serviceList: List<String>,
    val soldNum: Int,
//    val tipsMsgList: TipsMsg,
    val wishStatus: Boolean
)

data class ProductModel(
    var itemId: Int,
    var discountTxt: String,
    var marketPriceTxt: String,
    var picUrl: String,
    val promoName: String,
    val promoType: Int,
    val salePrice: String,
    var salePriceTxt: String,
    var sid: String,
    var tag: String,
    var title: String,
    val skuImageList: List<SkuImage>,
    val workId: Int
)

data class SkuImage(
    val colorImage: String,
    val propPath: String
)

class ProductDetailsItemSkuList : ArrayList<ProductDetailsItemSkuListItem>()

data class ProductDetailsItemSkuListItem(
    val discountTxt: String,
    val marketPrice: Double,
    val marketPriceTxt: String,
    val propPath: String,
    val quantity: Int,
    val salePrice: Double,
    val salePriceTxt: String,
    val skuId: Int
)

data class ProductDetailsItemSizeModel(
    val sizeCmList: List<List<String>>,
    val sizeHeaderList: List<String>,
    val sizeInchList: List<List<String>>,
    val sizeTipList: List<List<String>>
)

data class ProductDetailsItemReviews(
    val list: List<Reviews>,
    val reviewCount: Int,
    val reviewRate: Double
)

data class Reviews(
    val content: ReviewsContent,
    val gmtCreate: String,
    val id: Int,
    val rate: Int,
    val userIcon: String,
    val userName: String
)

data class ReviewsContent(
    val content: String,
    val images: List<String>
)

data class ProductDetailsItemBaseModel(
    val categoryId: Int,
    val colorImageList: List<ColorImage>,
    val discountTxt: String,
    val image: String,
    val imageList: List<String>,
    val itemId: String,
    val itemPoint: Int,
    val itemPropList: List<ItemProp>,
    val marketPrice: Int,
    val marketPriceTxt: String,
    val newFlag: Int,
    val publishStatus: Int,
    val recommandFlag: Int,
    val salePrice: Int,
    val salePriceTxt: String,
    val sid: String,
    val skuPropList: List<SkuProp>,
    val title: String,
    val usdPrice: Double
)

data class ColorImage(
    val colorImage: String,
    val propPath: String
)

data class ItemProp(
    val id: Int,
    val propName: String,
    val propValue: String
)

data class SkuProp(
    val isSizeAttr: Int,
    val name: String,
    val pid: Int,
    val values: List<Value>
)

data class Value(
    var active: Boolean,
    var disable: Boolean,
    val image: String,
    val name: String,
    val vid: Int
)

data class ServiceDetails(val detailList: List<String>, val title: String, val titleIcon: String)

class TipsMsgList : ArrayList<TipsMsg>()

data class TipsMsg(
    val detailList: List<String>,
    val id: Int,
    val title: String,
    val titleDesc: String,
    val titleUrl: String
)
