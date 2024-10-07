package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.domain.Location
import com.github.sipe90.lunchscraper.domain.MenuScrapeResult
import com.github.sipe90.lunchscraper.domain.Restaurant
import com.github.sipe90.lunchscraper.repository.ScrapeResultRepository
import com.github.sipe90.lunchscraper.util.Utils
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalTime
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MenuService(
    config: LunchScraperConfiguration,
    private val scrapeResultRepository: ScrapeResultRepository,
) {
    private val locations = config.locations
    private val restaurantsByRestaurantIdByLocationId = locations.mapValues { (_, location) -> location.restaurants }

    fun getLocationMenus(locationId: String): Location? {
        val location = locations[locationId] ?: return null
        val restaurants = location.restaurants.values

        val results =
            scrapeResultRepository.loadAllResults(Utils.getCurrentYear(), Utils.getCurrentWeek(), locationId).associateBy {
                it.restaurantId
            }

        return Location(
            name = location.name,
            restaurants =
                restaurants.map {
                    results[it.id]?.toDto() ?: Restaurant(name = it.name, url = it.urls.first())
                },
        )
    }

    private fun MenuScrapeResult.toDto(): Restaurant? {
        val restaurantConfig = restaurantsByRestaurantIdByLocationId[locationId]?.get(restaurantId)

        if (restaurantConfig == null) {
            logger.warn { "Couldn't find restaurant config for locationId: $locationId and restaurantId: $restaurantId" }
            return null
        }

        return extractionResult.lunchMenus!!.let {
            Restaurant(
                name = restaurantConfig.name,
                url = restaurantConfig.urls.first(),
                location = null,
                lunchtimeStart = it.lunchtimeStart?.let(LocalTime::parse),
                lunchtimeEnd = it.lunchtimeEnd?.let(LocalTime::parse),
                dailyMenus = it.dailyMenus,
            )
        }
    }
}
