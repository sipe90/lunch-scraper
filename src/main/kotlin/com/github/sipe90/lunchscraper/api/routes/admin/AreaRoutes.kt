package com.github.sipe90.lunchscraper.api.routes.admin

import com.github.sipe90.lunchscraper.api.LunchAreaApi
import com.github.sipe90.lunchscraper.api.dto.LunchAreaInput
import com.github.sipe90.lunchscraper.api.dto.LunchAreaUpdate
import com.github.sipe90.lunchscraper.api.dto.RestaurantInput
import com.github.sipe90.lunchscraper.api.dto.RestaurantUpdate
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail

private val logger = KotlinLogging.logger {}

fun Application.lunchAreaRoutes() {
    routing {
        route("/admin") {
            authenticate {
                route("/areas") {
                    get {
                        val lunchAreaApi: LunchAreaApi by dependencies

                        val areas = lunchAreaApi.getAllLunchAreas()
                        call.respond(areas)
                    }

                    post<LunchAreaInput> {
                        val lunchAreaApi: LunchAreaApi by dependencies

                        lunchAreaApi.createLunchArea(it)
                        call.respond(HttpStatusCode.OK)
                    }

                    route("/{areaId}") {
                        put<LunchAreaUpdate> {
                            val lunchAreaApi: LunchAreaApi by dependencies
                            val areaId = call.parameters.getOrFail("areaId")

                            lunchAreaApi.updateLunchArea(areaId, it)
                            call.respond(HttpStatusCode.OK)
                        }

                        delete {
                            val lunchAreaApi: LunchAreaApi by dependencies
                            val areaId = call.parameters.getOrFail("areaId")

                            lunchAreaApi.deleteLunchArea(areaId)
                            call.respond(HttpStatusCode.OK)
                        }

                        route("/restaurants") {
                            post<RestaurantInput> {
                                val lunchAreaApi: LunchAreaApi by dependencies
                                val areaId = call.parameters.getOrFail("areaId")

                                lunchAreaApi.addRestaurant(areaId, it)
                                call.respond(HttpStatusCode.OK)
                            }

                            route("/{restaurantId}") {
                                put<RestaurantUpdate> {
                                    val lunchAreaApi: LunchAreaApi by dependencies
                                    val areaId = call.parameters.getOrFail("areaId")
                                    val restaurantId = call.parameters.getOrFail("restaurantId")

                                    lunchAreaApi.updateRestaurant(areaId, restaurantId, it)
                                    call.respond(HttpStatusCode.OK)
                                }

                                delete {
                                    val lunchAreaApi: LunchAreaApi by dependencies
                                    val areaId = call.parameters.getOrFail("areaId")
                                    val restaurantId = call.parameters.getOrFail("restaurantId")

                                    lunchAreaApi.deleteRestaurant(areaId, restaurantId)
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
