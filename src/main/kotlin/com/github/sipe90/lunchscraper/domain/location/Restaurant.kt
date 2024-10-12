package com.github.sipe90.lunchscraper.domain.location

import com.github.sipe90.lunchscraper.api.dto.RestaurantOutput
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Restaurant(
    @SerialName("_id")
    val id: String,
    val name: String,
    val url: String? = null,
    val hint: String? = null,
    val parameters: ScrapeParameters,
) {
    fun toDto(): RestaurantOutput =
        RestaurantOutput(
            id = id,
            name = name,
            url = url,
            hint = hint,
            parameters = parameters,
        )
}
