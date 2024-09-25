package com.github.sipe90.jakelunch.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.util.AttributeKey
import org.springframework.context.ApplicationContext

class Configuration {
    lateinit var applicationContext: ApplicationContext
}

val key = AttributeKey<ApplicationContext>("applicationContext")

val SpringDIPlugin =
    createApplicationPlugin("SpringDIPlugin", ::Configuration) {
        application.attributes.put(key, pluginConfig.applicationContext)
    }

fun Application.configureSpringDI(ctx: ApplicationContext) {
    install(SpringDIPlugin) {
        applicationContext = ctx
    }
}

val Application.springContext: ApplicationContext
    get() = attributes[key]
