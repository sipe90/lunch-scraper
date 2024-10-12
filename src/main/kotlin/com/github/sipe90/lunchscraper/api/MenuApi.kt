package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.MenusOutput
import com.github.sipe90.lunchscraper.domain.location.Location
import com.github.sipe90.lunchscraper.domain.location.Restaurant
import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.scraping.ScrapeResultService
import com.github.sipe90.lunchscraper.settings.LocationService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalTime
import org.springframework.stereotype.Service

@Service
class MenuApi(
    private val locationService: LocationService,
    private val scrapeResultService: ScrapeResultService,
) {
    suspend fun getLocationMenus(locationId: String): MenusOutput? {
        val location = locationService.getLocation(locationId) ?: return null
        val results = scrapeResultService.getCurrentWeekResultsForLocation(locationId)

        return MenusOutput(
            location = location.toMenusDto(),
            restaurants =
                location.restaurants.map { rs ->
                    results.firstOrNull { it.restaurantId == rs.id }.toMenusDto(rs)
                },
        )
    }

    private fun Location.toMenusDto(): MenusOutput.Location = MenusOutput.Location(name = name)

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
