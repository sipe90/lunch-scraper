package com.github.sipe90.lunchscraper

import com.github.sipe90.lunchscraper.api.routes.admin.lunchAreaRoutes
import com.github.sipe90.lunchscraper.api.routes.admin.scrapeRoutes
import com.github.sipe90.lunchscraper.api.routes.admin.settingsRoutes
import com.github.sipe90.lunchscraper.api.routes.menuRoutes
import com.github.sipe90.lunchscraper.config.ApiConfig
import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.config.MongoDbConfig
import com.github.sipe90.lunchscraper.config.OpenAiConfig
import com.github.sipe90.lunchscraper.config.SettingsConfig
import com.github.sipe90.lunchscraper.plugins.configureSecurity
import com.github.sipe90.lunchscraper.plugins.configureSerialization
import com.github.sipe90.lunchscraper.plugins.configureSpringDI
import com.github.sipe90.lunchscraper.settings.SettingsService
import com.github.sipe90.lunchscraper.tasks.ScrapeScheduler
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import kotlinx.coroutines.launch
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.registerBean
import java.util.logging.Level
import java.util.logging.Logger

fun main(args: Array<String>) {
    Logger.getLogger("").level = Level.OFF
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    val config = LunchScraperConfiguration(environment.config.config("lunch-scraper"))

    val ctx =
        AnnotationConfigApplicationContext().also {
            it.registerBean<MongoDbConfig> { config.mongoDbConfig }
            it.registerBean<OpenAiConfig> { config.openAiConfig }
            it.registerBean<ApiConfig> { config.apiConfig }
            it.registerBean<SettingsConfig> { config.settingsConfig }

            it.scan("com.github.sipe90.lunchscraper")
            it.refresh()
        }

    val settingsServiceBean = ctx.getBean(SettingsService::class.java)
    val scrapeSchedulerBean = ctx.getBean(ScrapeScheduler::class.java)

    configureSecurity(config.apiConfig.apiKey)
    configureSerialization()
    configureSpringDI(ctx)

    menuRoutes()

    lunchAreaRoutes()
    settingsRoutes()
    scrapeRoutes()

    monitor.subscribe(ApplicationStarted) {
        launch {
            val settings = settingsServiceBean.getSettings()
            if (settings.scrape.enabled) {
                scrapeSchedulerBean.start(settings.scrape.schedule)
            }
        }
    }

    monitor.subscribe(ApplicationStopped) {
        if (scrapeSchedulerBean.isRunning()) {
            scrapeSchedulerBean.shutdown()
        }
    }
}
