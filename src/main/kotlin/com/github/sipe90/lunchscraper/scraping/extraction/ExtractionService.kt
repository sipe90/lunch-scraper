package com.github.sipe90.lunchscraper.scraping.extraction

import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.scraping.ScrapeParameters

interface ExtractionService {
    suspend fun extractMenusFromDocument(
        documents: List<String>,
        parameters: ScrapeParameters,
    ): MenuExtractionResult
}
