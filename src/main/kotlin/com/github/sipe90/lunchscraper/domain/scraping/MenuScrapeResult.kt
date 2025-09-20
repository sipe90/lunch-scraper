package com.github.sipe90.lunchscraper.domain.scraping

import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class MenuScrapeResult
    @OptIn(ExperimentalTime::class)
    constructor(
        val year: Int,
        val week: Int,
        val success: Boolean,
        val areaId: String,
        val restaurantId: String,
        val document: String? = null,
        val documentHash: String,
        val scrapeTimestamp: Instant,
        val extractionResult: MenuExtractionResult,
    )
