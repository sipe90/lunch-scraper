package com.github.sipe90.lunchscraper.html

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object DocumentLoader {
    suspend fun loadHtmlDocument(url: String): Document =
        createClient().use {
            val html = it.get(url).bodyAsText()
            Jsoup.parse(html)
        }

    private fun createClient() =
        HttpClient(CIO) {
            expectSuccess = true
        }
}
