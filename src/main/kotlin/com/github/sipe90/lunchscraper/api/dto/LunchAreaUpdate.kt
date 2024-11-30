package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.area.LunchArea
import kotlinx.serialization.Serializable

@Serializable
data class LunchAreaUpdate(
    val name: String,
) {
    fun toDomain(id: String): LunchArea =
        LunchArea(
            id = id,
            name = name,
            restaurants = emptyList(),
        )
}
