package com.github.sipe90.lunchscraper.scraping.scraper

import com.github.sipe90.lunchscraper.domain.area.HtmlScrapeParameters
import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.scraping.extraction.ExtractionService
import com.github.sipe90.lunchscraper.scraping.loader.HtmlDocumentCleaner
import com.github.sipe90.lunchscraper.scraping.loader.HtmlDocumentLoader
import com.github.sipe90.lunchscraper.util.Utils
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class HtmlScraper(
    private val extractionService: ExtractionService,
    private val settings: ScrapeSettings,
    private val scrapeParameters: HtmlScrapeParameters,
) : Scraper {
    private val logger = KotlinLogging.logger {}

    override suspend fun loadDocument(): String =
        coroutineScope {
            val htmlDocs =
                scrapeParameters.documents
                    .map {
                        async {
                            logger.info { "Loading an HTML document from ${it.url}" }

                            HtmlDocumentLoader.loadHtmlDocument(it.url).let {
                                HtmlDocumentCleaner.cleanDocument(it)
                            }
                        }
                    }.awaitAll()

            htmlDocs.joinToString("\n")
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
