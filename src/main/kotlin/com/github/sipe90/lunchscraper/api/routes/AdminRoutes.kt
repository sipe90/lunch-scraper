package com.github.sipe90.lunchscraper.api.routes

import com.github.sipe90.lunchscraper.api.AreaApi
import com.github.sipe90.lunchscraper.api.dto.AreaInput
import com.github.sipe90.lunchscraper.api.dto.AreaUpdate
import com.github.sipe90.lunchscraper.api.dto.RestaurantInput
import com.github.sipe90.lunchscraper.api.dto.RestaurantUpdate
import com.github.sipe90.lunchscraper.plugins.springContext
import com.github.sipe90.lunchscraper.scraping.ScrapeService
import com.github.sipe90.lunchscraper.tasks.ScrapeScheduler
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

fun Application.adminRoutes() {
    routing {
        route("/admin") {
            authenticate {
                route("/scheduler") {
                    post("/pause") {
                        val scrapeScheduler = springContext.getBean(ScrapeScheduler::class.java)
                        scrapeScheduler.pause()
                        call.respond(HttpStatusCode.OK)
                    }

                    post("/resume") {
                        val scrapeScheduler = springContext.getBean(ScrapeScheduler::class.java)
                        scrapeScheduler.resume()
                        call.respond(HttpStatusCode.OK)
                    }
                }

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
                        val areaId = call.parameters["areaId"]!!

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
                        val areaId = call.parameters["areaId"]!!
                        val restaurantId = call.parameters["restaurantId"]!!

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

                route("/areas") {

                    get {
                        val areaApi = springContext.getBean(AreaApi::class.java)

                        val areas = areaApi.getAllAreas()
                        call.respond(areas)
                    }

                    post<AreaInput> {
                        val areaApi = springContext.getBean(AreaApi::class.java)

                        areaApi.createArea(it)
                        call.respond(HttpStatusCode.OK)
                    }

                    route("/{areaId}") {

                        put<AreaUpdate> {
                            val areaApi = springContext.getBean(AreaApi::class.java)
                            val areaId = call.parameters["areaId"]!!

                            areaApi.updateArea(areaId, it)
                            call.respond(HttpStatusCode.OK)
                        }

                        delete {
                            val areaApi = springContext.getBean(AreaApi::class.java)
                            val areaId = call.parameters["areaId"]!!

                            areaApi.deleteArea(areaId)
                            call.respond(HttpStatusCode.OK)
                        }

                        route("/restaurants") {

                            post<RestaurantInput> {
                                val areaApi = springContext.getBean(AreaApi::class.java)
                                val areaId = call.parameters["areaId"]!!

                                areaApi.addRestaurant(areaId, it)
                                call.respond(HttpStatusCode.OK)
                            }

                            route("/{restaurantId}") {

                                put<RestaurantUpdate> {
                                    val areaApi = springContext.getBean(AreaApi::class.java)
                                    val areaId = call.parameters["areaId"]!!
                                    val restaurantId = call.parameters["restaurantId"]!!

                                    areaApi.updateRestaurant(areaId, restaurantId, it)
                                    call.respond(HttpStatusCode.OK)
                                }

                                delete {
                                    val areaApi = springContext.getBean(AreaApi::class.java)
                                    val areaId = call.parameters["areaId"]!!
                                    val restaurantId = call.parameters["restaurantId"]!!

                                    areaApi.deleteRestaurant(areaId, restaurantId)
                                    call.respond(HttpStatusCode.OK)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}