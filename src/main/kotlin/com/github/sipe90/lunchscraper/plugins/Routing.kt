package com.github.sipe90.lunchscraper.plugins

import com.github.sipe90.lunchscraper.scraping.ScrapeService
import com.github.sipe90.lunchscraper.service.MenuService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

fun Application.configureRouting() {
    routing {
        route("/menus") {
            get("/{locationId}") {
                val menuService = springContext.getBean(MenuService::class.java)
                val locationId = call.parameters.getOrFail("locationId")

                call.respond(menuService.getAllMenus(locationId))
            }

            get("/{locationId}/{restaurantId}") {
                val menuService = springContext.getBean(MenuService::class.java)
                val locationId = call.parameters.getOrFail("locationId")
                val restaurantId = call.parameters.getOrFail("restaurantId")

                val menus = menuService.getMenus(locationId, restaurantId)

                if (menus == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(menus)
                }
            }
        }

        route("/admin") {
            authenticate {
                route("/scrape") {
                    post {
                        val scrapeService = springContext.getBean(ScrapeService::class.java)
                        logger.info { "Scraping all menus" }
                        launch { scrapeService.scrapeAllMenus() }
                            .invokeOnCompletion {
                                when (it) {
                                    null -> {
                                        logger.info { "Finished scraping all menus" }
                                    }
                                    is CancellationException -> {
                                        logger.warn(it) { "Scraping cancelled for all menus" }
                                    }
                                    else -> {
                                        logger.error(it) { "Exception thrown while scraping all menus" }
                                    }
                                }
                            }

                        call.respond(HttpStatusCode.Accepted)
                    }

                    post("/{locationId}") {
                        val scrapeService = springContext.getBean(ScrapeService::class.java)
                        val locationId = call.parameters["locationId"]!!

                        logger.info { "Scraping all menus for location $locationId" }
                        launch { scrapeService.scrapeAllLocationMenus(locationId) }
                            .invokeOnCompletion {
                                when (it) {
                                    null -> {
                                        logger.info { "Finished scraping all menus for: $locationId" }
                                    }
                                    is CancellationException -> {
                                        logger.warn(it) { "Scraping cancelled for all menus for: $locationId" }
                                    }
                                    else -> {
                                        logger.error(it) { "Exception thrown while scraping all menus for: $locationId" }
                                    }
                                }
                            }

                        call.respond(HttpStatusCode.Accepted)
                    }

                    post("/{locationId}/{restaurantId}") {
                        val scrapeService = springContext.getBean(ScrapeService::class.java)
                        val locationId = call.parameters["locationId"]!!
                        val restaurantId = call.parameters["restaurantId"]!!

                        launch { scrapeService.scrapeRestaurantMenus(locationId, restaurantId) }
                            .invokeOnCompletion {
                                when (it) {
                                    null -> {
                                        logger.info { "Finished scraping menus for: $locationId/$restaurantId" }
                                    }
                                    is CancellationException -> {
                                        logger.warn(it) { "Scraping cancelled for: $locationId/$restaurantId" }
                                    }
                                    else -> {
                                        logger.error(it) { "Exception thrown while scraping menus for: $locationId/$restaurantId" }
                                    }
                                }
                            }

                        call.respond(HttpStatusCode.Accepted)
                    }
                }
            }
        }
    }
}
