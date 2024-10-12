package com.github.sipe90.lunchscraper.domain.location

import kotlinx.serialization.Serializable

@Serializable
data class JsonScrapeParameters(
    val urlTemplates: List<String>,
    val httpMethod: HttpMethod,
) : ScrapeParameters {
    override val type: ScrapeParameters.ScrapeType = ScrapeParameters.ScrapeType.Json
}
