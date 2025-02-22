package com.github.sipe90.lunchscraper.domain.area

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonScrapeParameters(
    override val type: ScrapeParameters.ScrapeType = ScrapeParameters.ScrapeType.Json,
    override val hint: String? = null,
    val urlTemplates: List<String>,
    val httpMethod: HttpMethod,
) : ScrapeParameters

enum class HttpMethod {
    @SerialName(value = "GET")
    GET,

    @SerialName(value = "POST")
    POST,
}
