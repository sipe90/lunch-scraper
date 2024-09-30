package com.github.sipe90.lunchscraper.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer

fun Application.configureSecurity(apiKey: String) {
    if (apiKey.isBlank() || apiKey.length < 16) {
        throw IllegalArgumentException("API key must be at least 16 characters long")
    }

    install(Authentication) {
        bearer {
            authenticate { tokenCredential ->
                if (tokenCredential.token == apiKey) {
                    UserIdPrincipal("admin")
                } else {
                    null
                }
            }
        }
    }
}
