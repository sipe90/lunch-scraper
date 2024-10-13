package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.MenusOutput
import com.github.sipe90.lunchscraper.area.AreaService
import com.github.sipe90.lunchscraper.domain.area.Area
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.scraping.ScrapeResultService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalTime
import org.springframework.stereotype.Service

@Service
class MenuApi(
    private val areaService: AreaService,
    private val scrapeResultService: ScrapeResultService,
) {
    suspend fun getAreaMenus(areaId: String): MenusOutput? {
        val area = areaService.getArea(areaId) ?: return null
        val results = scrapeResultService.getCurrentWeekResultsForArea(areaId)

        return MenusOutput(
            area = area.toMenusDto(),
            restaurants =
                area.restaurants.map { rs ->
                    results.firstOrNull { it.restaurantId == rs.id }.toMenusDto(rs)
                },
        )
    }

    private fun Area.toMenusDto(): MenusOutput.Area = MenusOutput.Area(name = name)

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
