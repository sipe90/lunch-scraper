package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.openapi.DailyMenus
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class MenusOutput(
    val location: Location,
    val restaurants: List<Restaurant>,
) {
    @Serializable
    data class Location(
        val name: String,
    )

    @Serializable
    data class Restaurant(
        val name: String,
        val url: String? = null,
        val location: String? = null,
        val lunchtimeStart: LocalTime? = null,
        val lunchtimeEnd: LocalTime? = null,
        val dailyMenus: DailyMenus? = null,
    )
}
