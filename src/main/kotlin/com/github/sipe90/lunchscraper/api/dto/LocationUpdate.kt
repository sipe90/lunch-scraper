package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.location.Location
import kotlinx.serialization.Serializable

@Serializable
data class LocationUpdate(
    val name: String,
    ) {
        fun toDomain(id: String): Location =
            Location(
                id = id,
                name = name,
                restaurants = emptyList(),
            )
    }
