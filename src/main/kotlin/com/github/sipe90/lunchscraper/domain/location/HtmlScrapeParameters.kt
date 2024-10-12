package com.github.sipe90.lunchscraper.domain.location

import kotlinx.serialization.Serializable

@Serializable
data class HtmlScrapeParameters(
    val documents: List<DocumentParameter>
) : ScrapeParameters {
    override val type: ScrapeParameters.ScrapeType = ScrapeParameters.ScrapeType.Html
}
