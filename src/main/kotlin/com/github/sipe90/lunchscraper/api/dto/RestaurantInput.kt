package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.area.Restaurant
import com.github.sipe90.lunchscraper.domain.area.ScrapeParameters
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantInput(
    val id: String,
    val name: String,
    val url: String,
    val parameters: ScrapeParameters,
) {
    fun toDomain(): Restaurant =
        Restaurant(
            id = id,
            name = name,
            url = url,
            parameters = parameters,
        )
}
