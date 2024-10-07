package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.domain.MenuScrapeResult
import com.github.sipe90.lunchscraper.domain.RestaurantMenus
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
    private val restaurantsByRestaurantIdByLocationId = config.locations.mapValues { (_, location) -> location.restaurants }

    fun getAllMenus(locationId: String): List<RestaurantMenus>? {
        val restaurants = restaurantsByRestaurantIdByLocationId[locationId]?.values ?: return null
        val results =
            scrapeResultRepository.loadAllResults(Utils.getCurrentYear(), Utils.getCurrentWeek(), locationId).associateBy {
                it.restaurantId
            }

        return restaurants.map {
            results[it.id]?.toDto() ?: RestaurantMenus(name = it.name, url = it.urls.first())
        }
    }

    fun getMenus(
        locationId: String,
        restaurantId: String,
    ): RestaurantMenus? {
        val restaurant = restaurantsByRestaurantIdByLocationId[locationId]?.get(restaurantId) ?: return null
        val result = scrapeResultRepository.loadResult(Utils.getCurrentYear(), Utils.getCurrentWeek(), locationId, restaurant.id)

        return if (result != null) {
            return result.toDto()
        } else {
            RestaurantMenus(name = restaurant.name, url = restaurant.urls.first())
        }
    }

    private fun MenuScrapeResult.toDto(): RestaurantMenus? {
        val restaurantConfig = restaurantsByRestaurantIdByLocationId[locationId]?.get(restaurantId)

        if (restaurantConfig == null) {
            logger.warn { "Couldn't find restaurant config for locationId: $locationId and restaurantId: $restaurantId" }
            return null
        }

        return extractionResult.lunchMenus!!.let {
            RestaurantMenus(
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
