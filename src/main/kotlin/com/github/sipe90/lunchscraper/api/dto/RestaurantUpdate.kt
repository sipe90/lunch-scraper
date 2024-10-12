package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.location.Restaurant
import com.github.sipe90.lunchscraper.domain.location.ScrapeParameters
import kotlinx.serialization.Serializable

@Serializable
class RestaurantUpdate(
    val name: String,
    val url: String,
    val parameters: ScrapeParameters,
) {
    fun toDomain(id: String): Restaurant =
        Restaurant(
            id = id,
            name = name,
            url = url,
            parameters = parameters,
        )
}
