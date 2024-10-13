package com.github.sipe90.lunchscraper.api.routes.admin

import com.github.sipe90.lunchscraper.plugins.springContext
import com.github.sipe90.lunchscraper.tasks.ScrapeScheduler
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.schedulerRoutes() {
    routing {
        route("/admin") {
            authenticate {
                route("/scheduler") {
                    get {
                        val scrapeScheduler = springContext.getBean(ScrapeScheduler::class.java)

                        call.respond(mapOf("isRunning" to scrapeScheduler.isRunning()))
                    }

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
            }
        }
    }
}
