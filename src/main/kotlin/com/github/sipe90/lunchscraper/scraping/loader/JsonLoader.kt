package com.github.sipe90.lunchscraper.scraping.loader

import com.github.sipe90.lunchscraper.domain.area.JsonScrapeParameters
import com.github.sipe90.lunchscraper.scraping.ScrapeParameters
import com.github.sipe90.lunchscraper.util.Utils
import com.github.sipe90.lunchscraper.util.toMap
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class JsonLoader(
    private val scrapeParameters: JsonScrapeParameters,
) : Loader {
    private val logger = KotlinLogging.logger {}

    override suspend fun loadDocuments(parameters: ScrapeParameters): List<String> =
        coroutineScope {
            scrapeParameters.urlTemplates
                .map {
                    async {
                        val url = Utils.replacePlaceholders(it, parameters.toMap())

                        logger.info { "Loading a JSON document from $url with a ${scrapeParameters.httpMethod} request" }

                        JsonDocumentLoader.loadJsonDocument(url, scrapeParameters.httpMethod.name).toString()
                    }
                }.awaitAll()
        }
}
