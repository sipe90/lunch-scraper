package com.github.sipe90.lunchscraper

import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.plugins.configureRouting
import com.github.sipe90.lunchscraper.plugins.configureSerialization
import com.github.sipe90.lunchscraper.plugins.configureSpringDI
import io.ktor.server.application.Application
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    val ctx =
        AnnotationConfigApplicationContext().also {
            it.registerBean(LunchScraperConfiguration::class.java, environment.config.config("lunch-scraper"))
            it.scan("com.github.sipe90.lunchscraper")
            it.refresh()
        }

    configureRouting()
    configureSerialization()
    configureSpringDI(ctx)
}
