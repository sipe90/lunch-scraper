package com.github.sipe90.lunchscraper.scraping.scraper

import com.github.sipe90.lunchscraper.domain.area.HtmlScrapeParameters
import com.github.sipe90.lunchscraper.domain.area.JsonScrapeParameters
import com.github.sipe90.lunchscraper.domain.area.ScrapeParameters
import com.github.sipe90.lunchscraper.scraping.extraction.ExtractionService
import com.github.sipe90.lunchscraper.settings.SettingsService
import org.springframework.stereotype.Component

@Component
class ScraperFactory(
    private val extractionService: ExtractionService,
    private val settingsService: SettingsService,
) {
    suspend fun create(scrapeParams: ScrapeParameters): Scraper {
        val scrapeSettings = settingsService.getSettings().scrape

        return when (scrapeParams) {
            is HtmlScrapeParameters -> HtmlScraper(extractionService, scrapeSettings, scrapeParams)
            is JsonScrapeParameters -> JsonScraper(extractionService, scrapeSettings, scrapeParams)
        }
    }
}
