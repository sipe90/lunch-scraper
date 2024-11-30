package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.area.LunchArea
import kotlinx.serialization.Serializable

@Serializable
data class LunchAreaInput(
    val id: String,
    val name: String,
) {
    fun toDomain(): LunchArea =
        LunchArea(
            id = id,
            name = name,
            restaurants = emptyList(),
        )
}
