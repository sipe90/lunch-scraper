package com.github.sipe90.lunchscraper.config

import io.ktor.server.config.ApplicationConfig

open class LunchScraperConfiguration(
    private val config: ApplicationConfig,
) {
    val apiConfig: ApiConfig
        get() = ApiConfig(config.config("api"))

    val mongoDbConfig: MongoDbConfig
        get() = MongoDbConfig(config.config("mongo-db"))

    val schedulerConfig: SchedulerConfig
        get() = SchedulerConfig(config.config("scheduler"))
}

open class MongoDbConfig(
    private val config: ApplicationConfig,
) {
    val url: String
        get() = config.property("url").getString()

    val database: String
        get() = config.property("database").getString()
}

open class ApiConfig(
    private val config: ApplicationConfig,
) {
    val apiKey: String
        get() = config.property("api-key").getString()
}

class SchedulerConfig(
    private val config: ApplicationConfig,
) {
    val enabled: Boolean
        get() = config.property("enabled").getString().toBoolean()

    val cron: String
        get() = config.property("cron").getString()
}
