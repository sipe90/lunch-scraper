package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.domain.location.HtmlScrapeParameters
import com.github.sipe90.lunchscraper.domain.location.Location
import com.github.sipe90.lunchscraper.domain.location.Restaurant
import com.github.sipe90.lunchscraper.domain.scraping.MenuScrapeResult
import com.github.sipe90.lunchscraper.html.DocumentCleaner
import com.github.sipe90.lunchscraper.html.DocumentLoader
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.settings.LocationService
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
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class ScrapeService(
    private val locationService: LocationService,
    private val scrapeResultService: ScrapeResultService,
    private val extractionService: ExtractionService,
) {
    suspend fun scrapeAllMenus() {
        val locations = locationService.getAllLocations()
        val previousResults = scrapeResultService.getCurrentWeekResults()

        locations.map { l ->
            val results = previousResults.filter { it.locationId == l.id }
            scrapeAllLocationMenus(l, results).awaitAll()
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    suspend fun scrapeAllLocationMenus(locationId: String) {
        val location =
            locationService.getLocation(locationId)
                ?: throw IllegalArgumentException("Location not found")

        val previousResults = scrapeResultService.getCurrentWeekResultsForLocation(locationId)
        scrapeAllLocationMenus(location, previousResults).awaitAll()
    }

    suspend fun scrapeRestaurantMenus(
        locationId: String,
        restaurantId: String,
    ) {
        val location =
            locationService.getLocation(locationId)
                ?: throw IllegalArgumentException("Location not found")
        val restaurant =
            location.restaurants.find { it.id == restaurantId }
                ?: throw IllegalArgumentException("Restaurant not found")

        val previousResult = scrapeResultService.getCurrentWeekResultsForLocationAndRestaurant(locationId, restaurantId)
        scrapeRestaurantMenus(location, restaurant, previousResult)
    }

    private suspend fun scrapeAllLocationMenus(
        location: Location,
        previousResults: Flow<MenuScrapeResult>,
    ) = coroutineScope {
        location.restaurants.map { rs ->
            async {
                val previousResult = previousResults.filter { it.locationId == rs.id }.firstOrNull()
                scrapeRestaurantMenus(location, rs, previousResult)
            }
        }
    }

    private suspend fun scrapeRestaurantMenus(
        location: Location,
        restaurant: Restaurant,
        previousResult: MenuScrapeResult?,
    ) = coroutineScope {
        try {
            if (restaurant.parameters !is HtmlScrapeParameters) {
                logger.info { "Skipping scrape for restaurant ${restaurant.id}" }
                return@coroutineScope
            }

            logger.info { "Scraping menus for restaurant ${restaurant.id}" }

            val htmlDocs =
                restaurant.parameters.documents
                    .map {
                        async {
                            DocumentLoader.loadHtmlDocument(it.url).let {
                                DocumentCleaner.cleanDocument(it)
                            }
                        }
                    }.awaitAll()

            val cleanedDocs = htmlDocs.joinToString("\n")
            val documentHash = cleanedDocs.md5()

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
                    "week" to Utils.getCurrentWeek().toString(),
                    "name" to restaurant.name,
                )
            var extractionResult = extractionService.extractMenusFromDocument(cleanedDocs, restaurant.hint, params)

            try {
                extractionResult = validateExtractionResult(extractionResult, previousResult?.extractionResult)

                logger.info { "Finished scraping menus for restaurant ${restaurant.id}" }

                val scrapeResult =
                    MenuScrapeResult(
                        year = Utils.getCurrentYear(),
                        week = Utils.getCurrentWeek(),
                        success = true,
                        locationId = location.id,
                        restaurantId = restaurant.id,
                        document = cleanedDocs,
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
                        locationId = location.id,
                        restaurantId = restaurant.id,
                        document = cleanedDocs,
                        documentHash = documentHash,
                        scrapeTimestamp = Clock.System.now(),
                        extractionResult = extractionResult,
                    )

                scrapeResultService.saveResult(scrapeResult)
                throw e
            }
        } catch (e: Exception) {
            logger.error(e) { "Exception thrown while trying to scrape menus for ${location.id}/${restaurant.id}" }
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
                                            ?: lunchMenus.dailyMenus.monday
                                    },
                                tuesday =
                                    lunchMenus.dailyMenus.tuesday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.tuesday
                                            ?: lunchMenus.dailyMenus.tuesday
                                    },
                                wednesday =
                                    lunchMenus.dailyMenus.wednesday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.wednesday
                                            ?: lunchMenus.dailyMenus.wednesday
                                    },
                                thursday =
                                    lunchMenus.dailyMenus.thursday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.thursday
                                            ?: lunchMenus.dailyMenus.thursday
                                    },
                                friday =
                                    lunchMenus.dailyMenus.friday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.friday
                                            ?: lunchMenus.dailyMenus.friday
                                    },
                                saturday =
                                    lunchMenus.dailyMenus.saturday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.saturday
                                            ?: lunchMenus.dailyMenus.saturday
                                    },
                                sunday =
                                    lunchMenus.dailyMenus.sunday.ifEmpty {
                                        previousExtractionResult?.lunchMenus?.dailyMenus?.sunday
                                            ?: lunchMenus.dailyMenus.sunday
                                    },
                            ),
                    )
                },
        )
    }
}
