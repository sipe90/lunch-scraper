package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.util.Utils
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ScrapeResultService(
    private val menuScrapeResultRepository: MenuScrapeResultRepository,
) {
    suspend fun getCurrentWeekResults(): Flow<MenuScrapeResult> =
        menuScrapeResultRepository.findByYearAndWeek(Utils.getCurrentYear(), Utils.getCurrentWeek())

    suspend fun getCurrentWeekResultsForArea(areaId: String): Flow<MenuScrapeResult> =
        menuScrapeResultRepository.findByYearWeekAndArea(Utils.getCurrentYear(), Utils.getCurrentWeek(), areaId)

    suspend fun getCurrentWeekResultsForAreaAndRestaurant(
        areaId: String,
        restaurantId: String,
    ): MenuScrapeResult? =
        menuScrapeResultRepository.findOneByYearWeekAreaAndRestaurant(
            Utils.getCurrentYear(),
            Utils.getCurrentWeek(),
            areaId,
            restaurantId,
        )

    suspend fun saveResult(result: MenuScrapeResult) {
        menuScrapeResultRepository.replaceOne(result)
    }
}
