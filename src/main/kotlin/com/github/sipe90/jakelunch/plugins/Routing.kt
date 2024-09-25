package com.github.sipe90.jakelunch.plugins

import com.github.sipe90.jakelunch.service.MenuService
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
    }
}
