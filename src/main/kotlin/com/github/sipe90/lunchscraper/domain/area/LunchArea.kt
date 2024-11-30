package com.github.sipe90.lunchscraper.domain.area

import com.github.sipe90.lunchscraper.api.dto.LunchAreaOutput
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LunchArea(
    @SerialName("_id")
    val id: String,
    val name: String,
    val restaurants: List<Restaurant>,
) {
    fun toDto(): LunchAreaOutput =
        LunchAreaOutput(
            id = id,
            name = name,
            restaurants = restaurants.map { it.toDto() },
        )
}
