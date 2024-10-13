package com.github.sipe90.lunchscraper.api.routes

import com.github.sipe90.lunchscraper.api.MenuApi
import com.github.sipe90.lunchscraper.plugins.springContext
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail

fun Application.menuRoutes() {
    routing {
        route("/menus") {
            get("/{areaId}") {
                val menuApi = springContext.getBean(MenuApi::class.java)
                val areaId = call.parameters.getOrFail("areaId")

                val menus = menuApi.getAreaMenus(areaId)
                if (menus == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(menus)
                }
            }
        }
    }
}
