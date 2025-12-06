package com.github.sipe90.lunchscraper

import com.github.sipe90.lunchscraper.api.LunchAreaApi
import com.github.sipe90.lunchscraper.api.MenuApi
import com.github.sipe90.lunchscraper.api.SettingsApi
import com.github.sipe90.lunchscraper.api.routes.admin.lunchAreaRoutes
import com.github.sipe90.lunchscraper.api.routes.admin.scrapeRoutes
import com.github.sipe90.lunchscraper.api.routes.admin.settingsRoutes
import com.github.sipe90.lunchscraper.api.routes.menuRoutes
import com.github.sipe90.lunchscraper.config.MongoDbConfig
import com.github.sipe90.lunchscraper.config.provideMongoClient
import com.github.sipe90.lunchscraper.config.provideMongoDatabase
import com.github.sipe90.lunchscraper.luncharea.LunchAreaRepository
import com.github.sipe90.lunchscraper.luncharea.LunchAreaRepositoryImpl
import com.github.sipe90.lunchscraper.luncharea.LunchAreaService
import com.github.sipe90.lunchscraper.openai.OpenAIService
import com.github.sipe90.lunchscraper.plugins.configureSecurity
import com.github.sipe90.lunchscraper.plugins.configureSerialization
import com.github.sipe90.lunchscraper.scraping.MenuScrapeResultRepository
import com.github.sipe90.lunchscraper.scraping.MenuScrapeResultRepositoryImpl
import com.github.sipe90.lunchscraper.scraping.ScrapeResultService
import com.github.sipe90.lunchscraper.scraping.ScrapeService
import com.github.sipe90.lunchscraper.scraping.extraction.ExtractionService
import com.github.sipe90.lunchscraper.scraping.scraper.ScraperFactory
import com.github.sipe90.lunchscraper.settings.SettingsRepository
import com.github.sipe90.lunchscraper.settings.SettingsRepositoryImpl
import com.github.sipe90.lunchscraper.settings.SettingsService
import com.github.sipe90.lunchscraper.tasks.ScrapeScheduler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.config.property
import io.ktor.server.plugins.di.dependencies
import kotlinx.coroutines.launch
import java.util.logging.Level
import java.util.logging.Logger

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    Logger.getLogger("").level = Level.OFF
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    val developmentMode: Boolean = property("ktor.development")
    if (developmentMode) {
        logger.warn { "Starting application in development mode" }
    }

    val mongoConfig: MongoDbConfig = property("lunch-scraper.mongo-db")

    dependencies {
        provide { provideMongoClient(mongoConfig.url) }
        provide { provideMongoDatabase(mongoConfig.database, resolve()) }

        provide<SettingsRepository>(SettingsRepositoryImpl::class)
        provide<MenuScrapeResultRepository>(MenuScrapeResultRepositoryImpl::class)
        provide<LunchAreaRepository>(LunchAreaRepositoryImpl::class)

        provide(LunchAreaApi::class)
        provide(MenuApi::class)
        provide(SettingsApi::class)

        provide(LunchAreaService::class)
        provide(OpenAIService::class)
        provide(SettingsService::class)
        provide(ExtractionService::class)
        provide(ScrapeResultService::class)
        provide(ScrapeService::class)
        provide(ScraperFactory::class)
        provide(ScrapeScheduler::class)
    }

    configureSecurity()
    configureSerialization()

    menuRoutes()

    lunchAreaRoutes()
    settingsRoutes()
    scrapeRoutes()

    monitor.subscribe(ApplicationStarted) {
        launch {
            val settingsService: SettingsService by dependencies
            val scheduler: ScrapeScheduler by dependencies

            val scrapeSettings = settingsService.getSettings().scrape
            if (scrapeSettings.enabled) {
                scheduler.start(scrapeSettings.schedule)
            }
        }
    }
}
