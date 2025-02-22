package com.github.sipe90.lunchscraper.domain.area

import kotlinx.serialization.Serializable

@Serializable
data class HtmlScrapeParameters(
    override val type: ScrapeParameters.ScrapeType = ScrapeParameters.ScrapeType.Html,
    override val hint: String? = null,
    val documents: List<DocumentParameter>,
) : ScrapeParameters
