package com.github.sipe90.lunchscraper.api.routes.admin

import com.github.sipe90.lunchscraper.api.AreaApi
import com.github.sipe90.lunchscraper.api.dto.AreaInput
import com.github.sipe90.lunchscraper.api.dto.AreaUpdate
import com.github.sipe90.lunchscraper.api.dto.RestaurantInput
import com.github.sipe90.lunchscraper.api.dto.RestaurantUpdate
import com.github.sipe90.lunchscraper.plugins.springContext
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
import io.ktor.server.util.getOrFail

private val logger = KotlinLogging.logger {}

fun Application.areaRoutes() {
    routing {
        route("/admin") {
            authenticate {
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
                            val areaId = call.parameters.getOrFail("areaId")

                            areaApi.updateArea(areaId, it)
                            call.respond(HttpStatusCode.OK)
                        }

                        delete {
                            val areaApi = springContext.getBean(AreaApi::class.java)
                            val areaId = call.parameters.getOrFail("areaId")

                            areaApi.deleteArea(areaId)
                            call.respond(HttpStatusCode.OK)
                        }

                        route("/restaurants") {
                            post<RestaurantInput> {
                                val areaApi = springContext.getBean(AreaApi::class.java)
                                val areaId = call.parameters.getOrFail("areaId")

                                areaApi.addRestaurant(areaId, it)
                                call.respond(HttpStatusCode.OK)
                            }

                            route("/{restaurantId}") {
                                put<RestaurantUpdate> {
                                    val areaApi = springContext.getBean(AreaApi::class.java)
                                    val areaId = call.parameters.getOrFail("areaId")
                                    val restaurantId = call.parameters.getOrFail("restaurantId")

                                    areaApi.updateRestaurant(areaId, restaurantId, it)
                                    call.respond(HttpStatusCode.OK)
                                }

                                delete {
                                    val areaApi = springContext.getBean(AreaApi::class.java)
                                    val areaId = call.parameters.getOrFail("areaId")
                                    val restaurantId = call.parameters.getOrFail("restaurantId")

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
