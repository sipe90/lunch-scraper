package com.github.sipe90.lunchscraper.scraping.loader

import com.github.sipe90.lunchscraper.domain.area.HtmlScrapeParameters
import com.github.sipe90.lunchscraper.scraping.ScrapeParameters
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class HtmlLoader(
    private val scrapeParameters: HtmlScrapeParameters,
) : Loader {
    private val logger = KotlinLogging.logger {}

    override suspend fun loadDocuments(parameters: ScrapeParameters): List<String> =
        coroutineScope {
            scrapeParameters.documents
                .map {
                    async {
                        logger.info { "Loading an HTML document from ${it.url}" }

                        HtmlDocumentLoader.loadHtmlDocument(it.url).let { html ->
                            logger.info { "Converting HTML document to markdown" }
                            Html2MdConverter.convertHtmlToMarkdown(html)
                        }
                    }
                }.awaitAll()
        }
}
