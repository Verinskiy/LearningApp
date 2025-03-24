package com.verinskij.news.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PLUGIN_IS_NOT_ENABLED")
@Serializable
enum class Language {
    @SerialName("ar")
    AR,

    @SerialName("de")
    DE,

    @SerialName("en")
    EN,

    @SerialName("es")
    ES,
}