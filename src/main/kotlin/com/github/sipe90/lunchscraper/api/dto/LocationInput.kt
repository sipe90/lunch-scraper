package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.location.Location
import kotlinx.serialization.Serializable

@Serializable
data class LocationInput(
    val id: String,
    val name: String,
) {
    fun toDomain(): Location =
        Location(
            id = id,
            name = name,
            restaurants = emptyList(),
        )
}
