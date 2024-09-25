package com.github.sipe90.jakelunch.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class HttpClientConfiguration {
    @Bean
    open fun httpClient(): HttpClient = HttpClient(CIO)
}
