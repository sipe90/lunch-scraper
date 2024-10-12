package com.github.sipe90.lunchscraper.domain.location

import com.github.sipe90.lunchscraper.api.dto.LocationOutput
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    @SerialName("_id")
    val id: String,
    val name: String,
    val restaurants: List<Restaurant>,
) {
    fun toDto(): LocationOutput =
        LocationOutput(
            id = id,
            name = name,
            restaurants = restaurants.map { it.toDto() },
        )
}
