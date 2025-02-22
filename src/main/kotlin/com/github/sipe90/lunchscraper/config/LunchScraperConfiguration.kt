package com.github.sipe90.lunchscraper.config

import io.ktor.server.config.ApplicationConfig

open class LunchScraperConfiguration(
    private val config: ApplicationConfig,
) {
    val openAiConfig: OpenAiConfig
        get() = OpenAiConfig(config.config("open-ai"))

    val apiConfig: ApiConfig
        get() = ApiConfig(config.config("api"))

    val mongoDbConfig: MongoDbConfig
        get() = MongoDbConfig(config.config("mongo-db"))
}

open class MongoDbConfig(
    private val config: ApplicationConfig,
) {
    val url: String
        get() = config.property("url").getString()

    val database: String
        get() = config.property("database").getString()
}

open class OpenAiConfig(
    private val config: ApplicationConfig,
) {
    val apiKey: String
        get() = config.property("api-key").getString()

    val baseUrl: String
        get() = config.property("base-url").getString()

    val model: String
        get() = config.property("model").getString()
}

open class ApiConfig(
    private val config: ApplicationConfig,
) {
    val apiKey: String
        get() = config.property("api-key").getString()
}
