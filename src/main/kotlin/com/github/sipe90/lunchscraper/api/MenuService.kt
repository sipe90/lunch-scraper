package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.domain.MenuScrapeResult
import com.github.sipe90.lunchscraper.domain.RestaurantMenus
import com.github.sipe90.lunchscraper.repository.MenuRepository
import com.github.sipe90.lunchscraper.util.Utils
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalTime
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class MenuService(
    config: LunchScraperConfiguration,
    private val menuRepository: MenuRepository,
) {
    private val restaurantsByRestaurantIdByLocationId = config.locations.mapValues { (_, location) -> location.restaurants }

    fun getAllMenus(locationId: String): List<RestaurantMenus> =
        menuRepository.loadAllMenus(Utils.getCurrentYear(), Utils.getCurrentWeek(), locationId).mapNotNull { it.toDto() }

    fun getMenus(
        locationId: String,
        restaurantId: String,
    ): RestaurantMenus? = menuRepository.loadMenus(Utils.getCurrentYear(), Utils.getCurrentWeek(), locationId, restaurantId)?.toDto()

    private fun MenuScrapeResult.toDto(): RestaurantMenus? {
        val restaurantConfig = restaurantsByRestaurantIdByLocationId[locationId]?.get(restaurantId)

        if (restaurantConfig == null) {
            logger.warn { "Couldn't find restaurant config for locationId: $locationId and restaurantId: $restaurantId" }
            return null
        }

        return extractionResult.lunchMenus.let {
            RestaurantMenus(
                name = restaurantConfig.name,
                location = null,
                lunchtimeStart = it.lunchtimeStart?.let(LocalTime::parse),
                lunchtimeEnd = it.lunchtimeEnd?.let(LocalTime::parse),
                dailyMenus = it.dailyMenus,
                weeklyMenu = it.weeklyMenu,
            )
        }
    }
}
