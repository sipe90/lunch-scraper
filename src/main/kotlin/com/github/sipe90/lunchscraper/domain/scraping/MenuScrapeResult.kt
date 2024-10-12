package com.github.sipe90.lunchscraper.domain.scraping

import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class MenuScrapeResult(
    val year: Int,
    val week: Int,
    val success: Boolean,
    val locationId: String,
    val restaurantId: String,
    val document: String? = null,
    val documentHash: String,
    val scrapeTimestamp: Instant,
    val extractionResult: MenuExtractionResult,
)
