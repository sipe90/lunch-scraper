package com.github.sipe90.lunchscraper.repository

import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.singleOrNull
import org.bson.BsonValue
import org.springframework.stereotype.Repository

@Repository
class MenuScrapeResultRepositoryImpl(
    private val database: MongoDatabase,
) : MenuScrapeResultRepository {
    private companion object {
        const val COLLECTION = "scrape-results"
    }

    override suspend fun findByYearAndWeek(
        year: Int,
        week: Int,
    ): Flow<MenuScrapeResult> {
        val filters =
            Filters.and(
                Filters.eq(MenuScrapeResult::year.name, year),
                Filters.eq(MenuScrapeResult::week.name, week),
                Filters.eq(MenuScrapeResult::success.name, true)
            )
        return collection().find(filters)
    }

    override suspend fun findByYearWeekAndLocation(
        year: Int,
        week: Int,
        locationId: String,
    ): Flow<MenuScrapeResult> {
        val filters =
            Filters.and(
                Filters.eq(MenuScrapeResult::year.name, year),
                Filters.eq(MenuScrapeResult::week.name, week),
                Filters.eq(MenuScrapeResult::success.name, true),
                Filters.eq(MenuScrapeResult::locationId.name, locationId),
            )
        return collection().find(filters)
    }

    override suspend fun findOneByYearWeekLocationAndRestaurant(
        year: Int,
        week: Int,
        locationId: String,
        restaurantId: String,
    ): MenuScrapeResult? {
        val filters =
            Filters.and(
                Filters.eq(MenuScrapeResult::year.name, year),
                Filters.eq(MenuScrapeResult::week.name, week),
                Filters.eq(MenuScrapeResult::success.name, true),
                Filters.eq(MenuScrapeResult::locationId.name, locationId),
                Filters.eq(MenuScrapeResult::restaurantId.name, restaurantId),
            )
        return collection().find(filters).singleOrNull()
    }

    override suspend fun replaceOne(scrapeResult: MenuScrapeResult): BsonValue? {
        val filters =
            Filters.and(
                Filters.eq(MenuScrapeResult::year.name, scrapeResult.year),
                Filters.eq(MenuScrapeResult::week.name, scrapeResult.week),
                Filters.eq(MenuScrapeResult::success.name, scrapeResult.success),
                Filters.eq(MenuScrapeResult::locationId.name, scrapeResult.locationId),
                Filters.eq(MenuScrapeResult::restaurantId.name, scrapeResult.restaurantId),
            )
        return collection().replaceOne(filters, scrapeResult, ReplaceOptions().upsert(true)).upsertedId
    }

    private fun collection() = database.getCollection<MenuScrapeResult>(COLLECTION)
}
