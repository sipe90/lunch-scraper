package com.github.sipe90.lunchscraper.scraping.html

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object DocumentLoader {
    suspend fun loadHtmlDocument(url: String): Document =
        useClient {
            val html = it.get(url).bodyAsText()
            Jsoup.parse(html)
        }

    private suspend fun <T> useClient(block: suspend (httpClient: HttpClient) -> T) =
        HttpClient(CIO) { expectSuccess = true }
            .use { block(it) }
}
