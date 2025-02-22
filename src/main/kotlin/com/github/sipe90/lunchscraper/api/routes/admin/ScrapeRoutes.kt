package com.github.sipe90.lunchscraper.api.routes.admin

import com.github.sipe90.lunchscraper.plugins.springContext
import com.github.sipe90.lunchscraper.scraping.ScrapeService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

fun Application.scrapeRoutes() {
    routing {
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

                    post("/{areaId}") {
                        val scrapeService = springContext.getBean(ScrapeService::class.java)
                        val areaId = call.parameters.getOrFail("areaId")

                        logger.info { "Scraping all menus for area $areaId" }
                        launch { scrapeService.scrapeAllAreaMenus(areaId) }
                            .invokeOnCompletion {
                                when (it) {
                                    null -> {
                                        logger.info { "Finished scraping all menus for: $areaId" }
                                    }
                                    is CancellationException -> {
                                        logger.warn(it) { "Scraping cancelled for all menus for: $areaId" }
                                    }
                                    else -> {
                                        logger.error(it) { "Exception thrown while scraping all menus for: $areaId" }
                                    }
                                }
                            }

                        call.respond(HttpStatusCode.Accepted)
                    }

                    post("/{areaId}/{restaurantId}") {
                        val scrapeService = springContext.getBean(ScrapeService::class.java)
                        val areaId = call.parameters.getOrFail("areaId")
                        val restaurantId = call.parameters.getOrFail("restaurantId")

                        launch { scrapeService.scrapeRestaurantMenus(areaId, restaurantId) }
                            .invokeOnCompletion {
                                when (it) {
                                    null -> {
                                        logger.info { "Finished scraping menus for: $areaId/$restaurantId" }
                                    }
                                    is CancellationException -> {
                                        logger.warn(it) { "Scraping cancelled for: $areaId/$restaurantId" }
                                    }
                                    else -> {
                                        logger.error(it) { "Exception thrown while scraping menus for: $areaId/$restaurantId" }
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
