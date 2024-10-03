package com.github.sipe90.lunchscraper.domain

import com.github.sipe90.lunchscraper.openapi.DailyMenus
import com.github.sipe90.lunchscraper.openapi.WeeklyMenu
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantMenus(
    val name: String,
    val location: String? = null,
    val lunchtimeStart: LocalTime? = null,
    val lunchtimeEnd: LocalTime? = null,
    val dailyMenus: DailyMenus,
    val weeklyMenu: WeeklyMenu? = null,
)
