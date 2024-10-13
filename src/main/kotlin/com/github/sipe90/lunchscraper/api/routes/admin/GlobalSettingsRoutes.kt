package com.github.sipe90.lunchscraper.api.routes.admin

import com.github.sipe90.lunchscraper.api.SettingsApi
import com.github.sipe90.lunchscraper.api.dto.GlobalSettingsInput
import com.github.sipe90.lunchscraper.plugins.springContext
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.globalSettingsRoutes() {
    routing {
        route("/admin") {
            authenticate {
                route("/settings") {
                    get {
                        val settingsApi = springContext.getBean(SettingsApi::class.java)

                        val settings = settingsApi.getGlobalSettings()
                        call.respond(settings)
                    }

                    post<GlobalSettingsInput> {
                        val settingsApi = springContext.getBean(SettingsApi::class.java)

                        settingsApi.updateGlobalSettings(it)
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}
