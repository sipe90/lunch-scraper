package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.area.Area
import kotlinx.serialization.Serializable

@Serializable
data class AreaUpdate(
    val name: String,
) {
    fun toDomain(id: String): Area =
        Area(
            id = id,
            name = name,
            restaurants = emptyList(),
        )
}
