package com.github.sipe90.jakelunch

import com.github.sipe90.jakelunch.plugins.configureRouting
import com.github.sipe90.jakelunch.plugins.configureSerialization
import com.github.sipe90.jakelunch.plugins.configureSpringDI
import io.ktor.server.application.Application
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    val ctx = AnnotationConfigApplicationContext("com.github.sipe90.jakelunch")

    configureRouting()
    configureSerialization()
    configureSpringDI(ctx)
}
