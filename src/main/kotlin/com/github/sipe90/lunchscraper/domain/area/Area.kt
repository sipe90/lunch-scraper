package com.github.sipe90.lunchscraper.domain.area

import com.github.sipe90.lunchscraper.api.dto.AreaOutput
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Area(
    @SerialName("_id")
    val id: String,
    val name: String,
    val restaurants: List<Restaurant>,
) {
    fun toDto(): AreaOutput =
        AreaOutput(
            id = id,
            name = name,
            restaurants = restaurants.map { it.toDto() },
        )
}
