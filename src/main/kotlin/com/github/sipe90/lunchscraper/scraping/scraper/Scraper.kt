package com.github.sipe90.lunchscraper.scraping.scraper

import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult

interface Scraper {
    suspend fun loadDocument(): String

    suspend fun extractData(
        document: String,
        params: Map<String, String> = emptyMap(),
    ): MenuExtractionResult
}
