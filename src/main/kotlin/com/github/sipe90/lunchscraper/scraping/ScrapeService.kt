package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.domain.area.LunchArea
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.luncharea.LunchAreaService
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.scraping.scraper.ScraperFactory
import com.github.sipe90.lunchscraper.util.Utils
import com.github.sipe90.lunchscraper.util.md5
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

class ScrapeService(
    private val lunchAreaService: LunchAreaService,
    private val scrapeResultService: ScrapeResultService,
    private val scraperFactory: ScraperFactory,
) {
    suspend fun scrapeAllMenus() {
        val areas = lunchAreaService.getAllLunchAreas()
        val previousResults = scrapeResultService.getCurrentWeekResults()

        areas
            .map { l ->
                val results = previousResults.filter { it.areaId == l.id }
                scrapeAllAreaMenus(l, results).awaitAll()
            }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    suspend fun scrapeAllAreaMenus(areaId: String) {
        val area =
            lunchAreaService.getArea(areaId)
                ?: throw IllegalArgumentException("Area not found")

        val previousResults = scrapeResultService.getCurrentWeekResultsForArea(areaId)
        scrapeAllAreaMenus(area, previousResults).awaitAll()
    }

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

        val previousResult = scrapeResultService.getCurrentWeekResultsForAreaAndRestaurant(areaId, restaurantId)
        scrapeRestaurantMenus(area, restaurant, previousResult)
    }

    private suspend fun scrapeAllAreaMenus(
        lunchArea: LunchArea,
        previousResults: Flow<MenuScrapeResult>,
    ) = coroutineScope {
        lunchArea.restaurants.map { rs ->
            async {
                val previousResult = previousResults.filter { it.areaId == rs.id }.firstOrNull()
                scrapeRestaurantMenus(lunchArea, rs, previousResult)
            }
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

            val scraper = scraperFactory.create(restaurant.parameters)
            val document = scraper.loadDocument()
            val documentHash = document.md5()

            if (previousResult != null) {
                if (previousResult.documentHash == documentHash) {
                    logger.info { "Skipping extraction for ${restaurant.id} since document hash matches with previous scrape result hash" }
                    return@coroutineScope
                }
                logger.info { "Document hash changed from previous scrape for ${restaurant.id}. Proceeding with scrape." }
            } else {
                logger.info { "No previous scrape result found for ${restaurant.id}. Proceeding with scrape." }
            }

            val params =
                mapOf(
                    "name" to restaurant.name,
                )

            var extractionResult = scraper.extractData(document, params)

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
                        document = document,
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
                        document = document,
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
        if (extractionResult.errors.isNotEmpty()) {
            throw IllegalStateException("Extraction failed. Model returned errors: ${extractionResult.errors}")
        }
        if (extractionResult.lunchMenus == null) {
            throw IllegalStateException("Extraction failed. Model did not provide explanation")
        }

        return extractionResult.copy(
            lunchMenus =
                extractionResult.lunchMenus.let { lunchMenus ->
                    lunchMenus.copy(
                        // Since structured output's JSON schema does not (yet) support string format, we'll have to validate the start and end times.
                        // https://platform.openai.com/docs/guides/structured-outputs/some-type-specific-keywords-are-not-yet-supported
                        lunchtimeStart =
                            lunchMenus.lunchtimeStart?.let { lunchtimeStart ->
                                runCatching { LocalTime.parse(lunchtimeStart) }.fold(
                                    onSuccess = { lunchtimeStart },
                                    onFailure = {
                                        logger.warn(it) { "Failed to parse lunch start time $lunchtimeStart" }
                                        null
                                    },
                                )
                            },
                        lunchtimeEnd =
                            lunchMenus.lunchtimeEnd?.let { lunchtimeEnd ->
                                runCatching { LocalTime.parse(lunchtimeEnd) }.fold(
                                    onSuccess = { lunchtimeEnd },
                                    onFailure = {
                                        logger.warn(it) { "Failed to parse lunch end time $lunchtimeEnd" }
                                        null
                                    },
                                )
                            },
                        // If the current extraction result is missing menu items, but the previous result has them, the previous result is used instead.
                        // This might happen if a restaurant omits menus for past days of the week.
                        dailyMenus =
                            lunchMenus.dailyMenus.copy(
                                monday =
                                    lunchMenus.dailyMenus.monday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.monday
                                            ?: emptyList()
                                    },
                                tuesday =
                                    lunchMenus.dailyMenus.tuesday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.tuesday
                                            ?: emptyList()
                                    },
                                wednesday =
                                    lunchMenus.dailyMenus.wednesday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.wednesday
                                            ?: emptyList()
                                    },
                                thursday =
                                    lunchMenus.dailyMenus.thursday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.thursday
                                            ?: emptyList()
                                    },
                                friday =
                                    lunchMenus.dailyMenus.friday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.friday
                                            ?: emptyList()
                                    },
                                saturday =
                                    lunchMenus.dailyMenus.saturday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.saturday
                                            ?: emptyList()
                                    },
                                sunday =
                                    lunchMenus.dailyMenus.sunday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.sunday
                                            ?: emptyList()
                                    },
                            ),
                    )
                },
        )
    }
}
