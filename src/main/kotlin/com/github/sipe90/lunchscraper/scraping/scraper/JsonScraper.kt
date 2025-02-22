package com.github.sipe90.lunchscraper.scraping.scraper

import com.github.sipe90.lunchscraper.domain.area.JsonScrapeParameters
import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.scraping.extraction.ExtractionService
import com.github.sipe90.lunchscraper.scraping.loader.JsonDocumentLoader
import com.github.sipe90.lunchscraper.util.Utils
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class JsonScraper(
    private val extractionService: ExtractionService,
    private val settings: ScrapeSettings,
    private val scrapeParameters: JsonScrapeParameters,
) : Scraper {
    private val logger = KotlinLogging.logger {}

    override suspend fun loadDocument(): String =
        coroutineScope {
            val jsonDocs =
                scrapeParameters.urlTemplates
                    .map {
                        async {
                            val url = Utils.replacePlaceholders(it)

                            logger.info { "Loading a JSON document from $url with a ${scrapeParameters.httpMethod} request" }

                            JsonDocumentLoader.loadJsonDocument(url, scrapeParameters.httpMethod.name)
                        }
                    }.awaitAll()

            jsonDocs.joinToString("\n")
        }

    override suspend fun extractData(
        document: String,
        params: Map<String, String>,
    ): MenuExtractionResult {
        val hint = scrapeParameters.hint ?: ""
        val userPrompt = Utils.replacePlaceholders(settings.userPromptPrefix, params) + " $hint $document"

        return extractionService.extractMenusFromDocument(settings.systemPrompt, userPrompt)
    }
}
