package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.repository.MenuScrapeResultRepository
import com.github.sipe90.lunchscraper.util.Utils
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ScrapeResultService(
    private val menuScrapeResultRepository: MenuScrapeResultRepository,
) {
    suspend fun getCurrentWeekResults(): Flow<MenuScrapeResult> =
        menuScrapeResultRepository.findByYearAndWeek(Utils.getCurrentYear(), Utils.getCurrentWeek())

    suspend fun getCurrentWeekResultsForLocation(locationId: String): Flow<MenuScrapeResult> =
        menuScrapeResultRepository.findByYearWeekAndLocation(Utils.getCurrentYear(), Utils.getCurrentWeek(), locationId)

    suspend fun getCurrentWeekResultsForLocationAndRestaurant(
        locationId: String,
        restaurantId: String,
    ): MenuScrapeResult? =
        menuScrapeResultRepository.findOneByYearWeekLocationAndRestaurant(
            Utils.getCurrentYear(),
            Utils.getCurrentWeek(),
            locationId,
            restaurantId,
        )

    suspend fun saveResult(result: MenuScrapeResult) {
        menuScrapeResultRepository.replaceOne(result)
    }
}
