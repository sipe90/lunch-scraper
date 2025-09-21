package com.github.sipe90.lunchscraper.scraping.loader

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

object HtmlDocumentLoader {
    suspend fun loadHtmlDocument(url: String): String =
        useClient {
            it.get(url).bodyAsText()
        }

    private suspend fun <T> useClient(block: suspend (httpClient: HttpClient) -> T) =
        HttpClient(CIO) { expectSuccess = true }
            .use { block(it) }
}
