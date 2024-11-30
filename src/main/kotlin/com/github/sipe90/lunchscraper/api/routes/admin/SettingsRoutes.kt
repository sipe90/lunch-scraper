package com.github.sipe90.lunchscraper.api.routes.admin

import com.github.sipe90.lunchscraper.api.SettingsApi
import com.github.sipe90.lunchscraper.api.dto.SettingsInput
import com.github.sipe90.lunchscraper.plugins.springContext
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.settingsRoutes() {
    routing {
        route("/admin") {
            authenticate {
                route("/settings") {
                    get {
                        val settingsApi = springContext.getBean(SettingsApi::class.java)

                        val settings = settingsApi.getSettings()
                        call.respond(settings)
                    }

                    put<SettingsInput> {
                        val settingsApi = springContext.getBean(SettingsApi::class.java)

                        settingsApi.updateSettings(it)
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}
