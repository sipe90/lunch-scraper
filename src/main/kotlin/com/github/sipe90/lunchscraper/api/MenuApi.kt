package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.LunchAreaOutput
import com.github.sipe90.lunchscraper.api.dto.MenusOutput
import com.github.sipe90.lunchscraper.domain.area.LunchArea
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.luncharea.LunchAreaService
import com.github.sipe90.lunchscraper.openapi.DayOfWeek
import com.github.sipe90.lunchscraper.openapi.MenuForASingleDay
import com.github.sipe90.lunchscraper.scraping.ScrapeResultService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

class MenuApi(
    private val lunchAreaService: LunchAreaService,
    private val scrapeResultService: ScrapeResultService,
) {
    suspend fun getLunchAreas(): Flow<LunchAreaOutput> = lunchAreaService.getAllLunchAreas().map { it.toDto() }

    suspend fun getLunchAreaMenus(areaId: String): MenusOutput? {
        val area = lunchAreaService.getArea(areaId) ?: return null
        val results = scrapeResultService.getCurrentWeekResultsForArea(areaId)

        return MenusOutput(
            lunchArea = area.toMenusDto(),
            restaurants =
                area.restaurants.map { rs ->
                    results.firstOrNull { it.restaurantId == rs.id }.toMenusDto(rs)
                },
        )
    }

    private fun LunchArea.toMenusDto(): MenusOutput.LunchArea = MenusOutput.LunchArea(id = id, name = name)

    private fun MenuScrapeResult?.toMenusDto(restaurant: Restaurant): MenusOutput.Restaurant =
        if (this != null) {
            extractionResult.lunchMenus!!.let { menus ->
                MenusOutput.Restaurant(
                    name = restaurant.name,
                    url = restaurant.url,
                    location = null,
                    dailyMenus =
                        MenusOutput.DailyMenus(
                            monday = menus.days.find { it.dayOfWeek == DayOfWeek.Monday }?.toMenusDto(),
                            tuesday = menus.days.find { it.dayOfWeek == DayOfWeek.Tuesday }?.toMenusDto(),
                            wednesday = menus.days.find { it.dayOfWeek == DayOfWeek.Wednesday }?.toMenusDto(),
                            thursday = menus.days.find { it.dayOfWeek == DayOfWeek.Thursday }?.toMenusDto(),
                            friday = menus.days.find { it.dayOfWeek == DayOfWeek.Friday }?.toMenusDto(),
                            saturday = menus.days.find { it.dayOfWeek == DayOfWeek.Saturday }?.toMenusDto(),
                            sunday = menus.days.find { it.dayOfWeek == DayOfWeek.Sunday }?.toMenusDto(),
                        ),
                )
            }
        } else {
            MenusOutput.Restaurant(name = restaurant.name, url = restaurant.url)
        }

    private fun MenuForASingleDay.toMenusDto(): MenusOutput.DayMenu =
        MenusOutput.DayMenu(
            buffetPrice = buffetPrice,
            dayOfWeek = dayOfWeek,
            items = items,
            lunchtimeEnd = lunchtimeEnd?.let(LocalTime::parse),
            lunchtimeStart = lunchtimeStart?.let(LocalTime::parse),
            menuType = menuType,
        )
}
