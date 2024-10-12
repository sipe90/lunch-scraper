package com.github.sipe90.lunchscraper.repository

import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import kotlinx.coroutines.flow.Flow
import org.bson.BsonValue

interface MenuScrapeResultRepository {
    suspend fun findByYearAndWeek(
        year: Int,
        week: Int,
    ): Flow<MenuScrapeResult>

    suspend fun findByYearWeekAndLocation(
        year: Int,
        week: Int,
        locationId: String,
    ): Flow<MenuScrapeResult>

    suspend fun findOneByYearWeekLocationAndRestaurant(
        year: Int,
        week: Int,
        locationId: String,
        restaurantId: String,
    ): MenuScrapeResult?

    suspend fun replaceOne(scrapeResult: MenuScrapeResult): BsonValue?
}
