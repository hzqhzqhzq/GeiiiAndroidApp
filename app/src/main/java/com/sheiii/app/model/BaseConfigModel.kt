package com.sheiii.app.model

data class BaseConfigModel(
    val contact: Contact,
    var countryCode: String,
    var currencyCode: String,
    var currencyListStatus: Boolean,
    var languageCode: String,
    var languageListStatus: Boolean,
    var languageName: String,
    var memberId: String,
    var sessionId: String,
    var siteCode: String,
    var uuid: String
)

data class Contact(
    var goUrl: String,
    var hasChat: Boolean,
    var iconUrl: String
)