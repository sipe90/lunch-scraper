package com.github.sipe90.lunchscraper.scraping.loader

import com.github.sipe90.lunchscraper.scraping.ScrapeParameters

interface Loader {
    suspend fun loadDocuments(parameters: ScrapeParameters): List<String>
}
