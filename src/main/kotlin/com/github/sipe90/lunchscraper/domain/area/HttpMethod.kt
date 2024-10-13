package com.github.sipe90.lunchscraper.domain.area

import kotlinx.serialization.SerialName

enum class HttpMethod {
    @SerialName(value = "GET")
    GET,

    @SerialName(value = "POST")
    POST,
}
