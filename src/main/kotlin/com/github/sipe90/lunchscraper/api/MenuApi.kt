package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.LunchAreaOutput
import com.github.sipe90.lunchscraper.api.dto.MenusOutput
import com.github.sipe90.lunchscraper.domain.area.LunchArea
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.luncharea.LunchAreaService
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
            extractionResult.lunchMenus!!.let {
                MenusOutput.Restaurant(
                    name = restaurant.name,
                    url = restaurant.url,
                    location = null,
                    lunchtimeStart = it.lunchtimeStart?.let(LocalTime::parse),
                    lunchtimeEnd = it.lunchtimeEnd?.let(LocalTime::parse),
                    dailyMenus = it.dailyMenus,
                )
            }
        } else {
            MenusOutput.Restaurant(name = restaurant.name, url = restaurant.url)
        }
}
