package com.github.sipe90.lunchscraper.scraping.loader

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.JsonObject

object JsonDocumentLoader {
    suspend fun loadJsonDocument(
        url: String,
        method: String,
    ): JsonObject =
        useClient {
            it
                .request(url) {
                    this.method = HttpMethod.parse(method)
                }.body<JsonObject>()
        }

    private suspend fun <T : Any> useClient(block: suspend (httpClient: HttpClient) -> T) =
        HttpClient(CIO) {
            expectSuccess = true

            install(ContentNegotiation) {
                json()
            }
        }.use { block(it) }
}
