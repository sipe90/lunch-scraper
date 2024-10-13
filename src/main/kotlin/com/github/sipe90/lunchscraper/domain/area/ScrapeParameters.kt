package com.github.sipe90.lunchscraper.domain.area

import com.github.sipe90.lunchscraper.serialization.ScrapeParametersSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = ScrapeParametersSerializer::class)
sealed interface ScrapeParameters {
    val type: ScrapeType

    @Serializable
    enum class ScrapeType(val value: String) {
        @SerialName(value = "html")
        Html("html"),

        @SerialName(value = "json")
        Json("json"),
    }
}
