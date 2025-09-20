package com.github.sipe90.lunchscraper.api.routes.admin

import com.github.sipe90.lunchscraper.api.SettingsApi
import com.github.sipe90.lunchscraper.api.dto.SettingsInput
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.di.dependencies
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
                        val settingsApi: SettingsApi by dependencies

                        val settings = settingsApi.getSettings()
                        call.respond(settings)
                    }

                    put<SettingsInput> {
                        val settingsApi: SettingsApi by dependencies

                        settingsApi.updateSettings(it)
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}
