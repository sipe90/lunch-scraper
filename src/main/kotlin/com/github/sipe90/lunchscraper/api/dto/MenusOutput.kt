package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.openapi.DayOfWeek
import com.github.sipe90.lunchscraper.openapi.MenuItem
import com.github.sipe90.lunchscraper.openapi.MenuType
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class MenusOutput(
    val lunchArea: LunchArea,
    val restaurants: List<Restaurant>,
) {
    @Serializable
    data class LunchArea(
        val id: String,
        val name: String,
    )

    @Serializable
    data class Restaurant(
        val name: String,
        val url: String? = null,
        val location: String? = null,
        val dailyMenus: DailyMenus? = null,
    )

    @Serializable
    data class DailyMenus(
        val monday: DayMenu? = null,
        val tuesday: DayMenu? = null,
        val wednesday: DayMenu? = null,
        val thursday: DayMenu? = null,
        val friday: DayMenu? = null,
        val saturday: DayMenu? = null,
        val sunday: DayMenu? = null,
    )

    @Serializable
    data class DayMenu(
        val buffetPrice: Double? = null,
        val dayOfWeek: DayOfWeek,
        val items: List<MenuItem>,
        val lunchtimeEnd: LocalTime? = null,
        val lunchtimeStart: LocalTime? = null,
        val menuType: MenuType,
    )
}
