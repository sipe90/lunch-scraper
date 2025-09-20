package com.github.sipe90.lunchscraper.api.routes

import com.github.sipe90.lunchscraper.api.MenuApi
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail

fun Application.menuRoutes() {
    routing {
        route("/areas") {
            get {
                val menuApi: MenuApi by dependencies

                val lunchAreas = menuApi.getLunchAreas()
                call.respond(lunchAreas)
            }

            get("/{areaId}") {
                val menuApi: MenuApi by dependencies
                val areaId = call.parameters.getOrFail("areaId")

                val menus = menuApi.getLunchAreaMenus(areaId)
                if (menus == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(menus)
                }
            }
        }
    }
}
