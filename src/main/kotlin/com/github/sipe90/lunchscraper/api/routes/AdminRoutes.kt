package com.github.sipe90.lunchscraper.api.routes

import com.github.sipe90.lunchscraper.api.LocationApi
import com.github.sipe90.lunchscraper.api.dto.LocationInput
import com.github.sipe90.lunchscraper.api.dto.LocationUpdate
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

                route("/locations") {

                    get {
                        val locationApi = springContext.getBean(LocationApi::class.java)

                        val locations = locationApi.getAllLocations()
                        call.respond(locations)
                    }

                    post<LocationInput> {
                        val locationApi = springContext.getBean(LocationApi::class.java)

                        locationApi.createLocation(it)
                        call.respond(HttpStatusCode.OK)
                    }

                    route("/{locationId}") {

                        put<LocationUpdate> {
                            val locationApi = springContext.getBean(LocationApi::class.java)
                            val locationId = call.parameters["locationId"]!!

                            locationApi.updateLocation(locationId, it)
                            call.respond(HttpStatusCode.OK)
                        }

                        delete {
                            val locationApi = springContext.getBean(LocationApi::class.java)
                            val locationId = call.parameters["locationId"]!!

                            locationApi.deleteLocation(locationId)
                            call.respond(HttpStatusCode.OK)
                        }

                        route("/restaurants") {

                            post<RestaurantInput> {
                                val locationApi = springContext.getBean(LocationApi::class.java)
                                val locationId = call.parameters["locationId"]!!

                                locationApi.addRestaurant(locationId, it)
                                call.respond(HttpStatusCode.OK)
                            }

                            route("/{restaurantId}") {

                                put<RestaurantUpdate> {
                                    val locationApi = springContext.getBean(LocationApi::class.java)
                                    val locationId = call.parameters["locationId"]!!
                                    val restaurantId = call.parameters["restaurantId"]!!

                                    locationApi.updateRestaurant(locationId, restaurantId, it)
                                    call.respond(HttpStatusCode.OK)
                                }

                                delete {
                                    val locationApi = springContext.getBean(LocationApi::class.java)
                                    val locationId = call.parameters["locationId"]!!
                                    val restaurantId = call.parameters["restaurantId"]!!

                                    locationApi.deleteRestaurant(locationId, restaurantId)
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