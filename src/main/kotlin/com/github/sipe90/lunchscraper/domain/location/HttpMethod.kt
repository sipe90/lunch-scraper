package com.github.sipe90.lunchscraper.domain.location

import kotlinx.serialization.SerialName

enum class HttpMethod {
    @SerialName(value = "GET")
    GET,

    @SerialName(value = "POST")
    POST,
}
