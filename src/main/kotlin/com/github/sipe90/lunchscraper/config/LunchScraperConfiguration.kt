package com.github.sipe90.lunchscraper.config

import io.ktor.server.config.ApplicationConfig

class LunchScraperConfiguration(
    private val applicationConfig: ApplicationConfig,
) {
    val apiConfig: ApiConfig
        get() = ApiConfig(applicationConfig.config("api"))

    val openAiConfig: OpenAIConfig
        get() = OpenAIConfig(applicationConfig.config("open-ai"))

    val schedulerConfig: SchedulerConfig
        get() = SchedulerConfig(applicationConfig.config("scheduler"))

    val scrapingConfig: ScrapingConfig
        get() = ScrapingConfig(applicationConfig.config("scraping"))

    val locations: Map<String, LocationConfig>
        get() =
            applicationConfig
                .configList("locations")
                .map { LocationConfig(it) }
                .associateBy { it.id }
}

class ApiConfig(
    private val config: ApplicationConfig,
) {
    val apiKey: String
        get() = config.property("api-key").getString()
}

class OpenAIConfig(
    private val applicationConfig: ApplicationConfig,
) {
    val baseUrl: String
        get() = applicationConfig.property("base-url").getString()

    val model: String
        get() = applicationConfig.property("model").getString()

    val apiKey: String
        get() = applicationConfig.property("api-key").getString()
}

class SchedulerConfig(
    private val config: ApplicationConfig,
) {
    val enabled: Boolean
        get() = config.property("enabled").getString().toBoolean()

    val cron: String
        get() = config.property("cron").getString()
}

class ScrapingConfig(
    private val applicationConfig: ApplicationConfig,
) {
    val systemPrompt: String
        get() = applicationConfig.property("system-prompt").getString()

    val userPromptPrefix: String
        get() = applicationConfig.property("user-prompt-prefix").getString()

    val saveDocument: Boolean
        get() = applicationConfig.propertyOrNull("save-document")?.getString()?.toBoolean() ?: false
}

class LocationConfig(
    private val applicationConfig: ApplicationConfig,
) {
    val id: String
        get() = applicationConfig.property("id").getString()

    val name: String
        get() = applicationConfig.property("name").getString()

    val restaurants: Map<String, RestaurantConfig>
        get() =
            applicationConfig
                .configList("restaurants")
                .map { RestaurantConfig(it) }
                .associateBy { it.id }
}

class RestaurantConfig(
    private val applicationConfig: ApplicationConfig,
) {
    val id: String
        get() = applicationConfig.property("id").getString()

    val name: String
        get() = applicationConfig.property("name").getString()

    val urls: List<String>
        get() = applicationConfig.property("urls").getList()

    val hint: String?
        get() = applicationConfig.propertyOrNull("hint")?.getString()
}
