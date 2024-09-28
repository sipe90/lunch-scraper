package com.github.sipe90.lunchscraper.plugins

import com.github.sipe90.lunchscraper.service.MenuService
import com.github.sipe90.lunchscraper.service.ScrapeService
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/") {
            val menuService = springContext.getBean(MenuService::class.java)

            call.respond(menuService.getMenus())
        }

        get("/test") {
            val scrapeService = springContext.getBean(ScrapeService::class.java)
            call.respond(scrapeService.scrape())
        }
    }
}
