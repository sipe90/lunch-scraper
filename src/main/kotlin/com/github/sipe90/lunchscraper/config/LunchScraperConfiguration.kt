package com.github.sipe90.lunchscraper.config

import io.ktor.server.config.ApplicationConfig

class LunchScraperConfiguration(
    private val applicationConfig: ApplicationConfig,
) {
    val openAiConfig: OpenAIConfig
        get() = OpenAIConfig(applicationConfig.config("open-ai"))

    inner class OpenAIConfig(
        private val applicationConfig: ApplicationConfig,
    ) {
        val baseUrl: String
            get() = applicationConfig.property("base-url").getString()

        val model: String
            get() = applicationConfig.property("model").getString()

        val apiKey: String
            get() = applicationConfig.property("api-key").getString()
    }
}
