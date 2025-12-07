package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.domain.area.LunchArea
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.luncharea.LunchAreaService
import com.github.sipe90.lunchscraper.openapi.DayOfWeek
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.openapi.Severity
import com.github.sipe90.lunchscraper.scraping.extraction.ExtractionService
import com.github.sipe90.lunchscraper.scraping.loader.LoaderFactory
import com.github.sipe90.lunchscraper.util.Utils
import com.github.sipe90.lunchscraper.util.md5
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

class ScrapeService(
    private val lunchAreaService: LunchAreaService,
    private val scrapeResultService: ScrapeResultService,
    private val extractionService: ExtractionService,
) {
    /**
     * Scrapes all restaurants in all areas sequentially.
     */
    suspend fun scrapeAllMenus() {
        val areas = lunchAreaService.getAllLunchAreas()
        val previousResults = scrapeResultService.getCurrentWeekResults()

        areas.collect { area ->
            val resultsForArea = previousResults.filter { it.areaId == area.id }
            scrapeAllAreaMenus(area, resultsForArea) // <- sequential inside as well
        }
    }

    /**
     * Scrapes all restaurants in a single are sequentially.
     */
    suspend fun scrapeAllAreaMenus(areaId: String) {
        val area =
            lunchAreaService.getArea(areaId)
                ?: throw IllegalArgumentException("Area not found")

        val previousResults = scrapeResultService.getCurrentWeekResultsForArea(areaId)
        scrapeAllAreaMenus(area, previousResults)
    }

    /**
     * Scrapes a single restaurant.
     */
    suspend fun scrapeRestaurantMenus(
        areaId: String,
        restaurantId: String,
    ) {
        val area =
            lunchAreaService.getArea(areaId)
                ?: throw IllegalArgumentException("Area not found")
        val restaurant =
            area.restaurants.find { it.id == restaurantId }
                ?: throw IllegalArgumentException("Restaurant not found")

        val previousResult =
            scrapeResultService.getCurrentWeekResultsForAreaAndRestaurant(areaId, restaurantId)

        scrapeRestaurantMenus(area, restaurant, previousResult)
    }

    private suspend fun scrapeAllAreaMenus(
        lunchArea: LunchArea,
        previousResults: Flow<MenuScrapeResult>,
    ) {
        for (rs in lunchArea.restaurants) {
            val previousResult =
                previousResults
                    .filter { it.restaurantId == rs.id }
                    .firstOrNull()

            scrapeRestaurantMenus(lunchArea, rs, previousResult)
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun scrapeRestaurantMenus(
        lunchArea: LunchArea,
        restaurant: Restaurant,
        previousResult: MenuScrapeResult?,
    ) = coroutineScope {
        try {
            logger.info { "Scraping menus for restaurant ${restaurant.id}" }

            val params =
                ScrapeParameters(
                    name = restaurant.name,
                )

            val scraper = LoaderFactory.create(restaurant.parameters)
            val documents = scraper.loadDocuments(params)
            val documentHash = documents.md5()

            if (previousResult != null) {
                if (previousResult.documentHash == documentHash) {
                    logger.info { "Skipping extraction for ${restaurant.id} since document hash matches with previous scrape result hash" }
                    return@coroutineScope
                }
                logger.info { "Document hash changed from previous scrape for ${restaurant.id}. Proceeding with scrape." }
            } else {
                logger.info { "No previous scrape result found for ${restaurant.id}. Proceeding with scrape." }
            }

            var extractionResult = extractionService.extractMenusFromDocument(documents, params)

            try {
                extractionResult = validateExtractionResult(extractionResult, previousResult?.extractionResult)

                logger.info { "Finished scraping menus for restaurant ${restaurant.id}" }

                val scrapeResult =
                    MenuScrapeResult(
                        year = Utils.getCurrentYear(),
                        week = Utils.getCurrentWeek(),
                        success = true,
                        areaId = lunchArea.id,
                        restaurantId = restaurant.id,
                        documents = documents,
                        documentHash = documentHash,
                        scrapeTimestamp = Clock.System.now(),
                        extractionResult = extractionResult,
                    )

                scrapeResultService.saveResult(scrapeResult)
            } catch (e: Exception) {
                val scrapeResult =
                    MenuScrapeResult(
                        year = Utils.getCurrentYear(),
                        week = Utils.getCurrentWeek(),
                        success = false,
                        areaId = lunchArea.id,
                        restaurantId = restaurant.id,
                        documents = documents,
                        documentHash = documentHash,
                        scrapeTimestamp = Clock.System.now(),
                        extractionResult = extractionResult,
                    )

                scrapeResultService.saveResult(scrapeResult)
                throw e
            }
        } catch (e: Exception) {
            logger.error(e) { "Exception thrown while trying to scrape menus for ${lunchArea.id}/${restaurant.id}" }
        }
    }

    private fun validateExtractionResult(
        extractionResult: MenuExtractionResult,
        previousExtractionResult: MenuExtractionResult?,
    ): MenuExtractionResult {
        if (extractionResult.errors.any { it.severity === Severity.Error }) {
            throw IllegalStateException("Extraction failed. Model returned errors: ${extractionResult.errors}")
        }
        if (extractionResult.lunchMenus == null) {
            throw IllegalStateException("Extraction failed. Model did not provide explanation")
        }
        DayOfWeek.entries.forEach { dayOfWeek ->
            if (extractionResult.lunchMenus.days
                    .filter { it.dayOfWeek === dayOfWeek }
                    .size > 1
            ) {
                throw IllegalStateException("Extraction failed. Found multiple instances of same day: $dayOfWeek")
            }
        }

        // If the current extraction result is missing menu items, but the previous result has them, the previous result is used instead.
        // This might happen if a restaurant omits menus for past days of the week.
        val previousResultDayMenus = previousExtractionResult?.lunchMenus?.days?.associateBy { it.dayOfWeek } ?: emptyMap()
        val dayMenus = extractionResult.lunchMenus.days.associateBy { it.dayOfWeek }

        val mergedDayMenus = previousResultDayMenus + dayMenus

        return extractionResult.copy(
            lunchMenus = extractionResult.lunchMenus.copy(days = mergedDayMenus.values.toList()),
        )
    }
}
