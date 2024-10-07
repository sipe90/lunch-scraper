package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.config.LocationConfig
import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.config.RestaurantConfig
import com.github.sipe90.lunchscraper.domain.MenuScrapeResult
import com.github.sipe90.lunchscraper.html.DocumentCleaner
import com.github.sipe90.lunchscraper.html.DocumentLoader
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.repository.ScrapeResultRepository
import com.github.sipe90.lunchscraper.util.Utils
import com.github.sipe90.lunchscraper.util.md5
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class ScrapeService(
    config: LunchScraperConfiguration,
    private val extractionService: ExtractionService,
    private val scrapeResultRepository: ScrapeResultRepository,
) {
    private val saveDocument = config.scrapingConfig.saveDocument

    private val locations = config.locations

    suspend fun scrapeAllMenus() {
        locations.values.map { scrapeAllLocationMenus(it).awaitAll() }
    }

    suspend fun scrapeAllLocationMenus(locationId: String) {
        val location = locations[locationId] ?: return
        scrapeAllLocationMenus(location).awaitAll()
    }

    suspend fun scrapeRestaurantMenus(
        locationId: String,
        restaurantId: String,
    ) {
        val location = locations[locationId] ?: return
        val restaurant = location.restaurants[restaurantId] ?: return

        scrapeRestaurantMenus(location, restaurant)
    }

    private suspend fun scrapeAllLocationMenus(location: LocationConfig) =
        coroutineScope {
            location.restaurants.values.map {
                async { scrapeRestaurantMenus(location, it) }
            }
        }

    private suspend fun scrapeRestaurantMenus(
        location: LocationConfig,
        restaurant: RestaurantConfig,
    ) = coroutineScope {
        try {
            logger.info { "Scraping menus for restaurant ${restaurant.id}" }

            val existingScrapeResult = scrapeResultRepository.loadResult(locationId = location.id, restaurantId = restaurant.id)

            val htmlDocs =
                restaurant.urls
                    .map {
                        async {
                            DocumentLoader.loadHtmlDocument(it).let {
                                DocumentCleaner.cleanDocument(it)
                            }
                        }
                    }.awaitAll()

            val cleanedDocs = htmlDocs.joinToString("\n")
            val documentHash = cleanedDocs.md5()

            if (existingScrapeResult != null) {
                if (existingScrapeResult.documentHash == documentHash) {
                    logger.info { "Skipping extraction for ${restaurant.id} since document hash matches with previous scrape result hash" }
                    return@coroutineScope
                }
                logger.info { "Document hash changed from previous scrape for ${restaurant.id}. Proceeding with scrape." }
            } else {
                logger.info { "No previous scrape result found for ${restaurant.id}. Proceeding with scrape." }
            }

            val params = mapOf("week" to Utils.getCurrentWeek().toString())
            var extractionResult = extractionService.extractMenusFromDocument(cleanedDocs, restaurant.hint, params)

            try {
                extractionResult = validateExtractionResult(extractionResult)

                logger.info { "Finished scraping menus for restaurant ${restaurant.id}" }

                val scrapeResult =
                    MenuScrapeResult(
                        year = Utils.getCurrentYear(),
                        week = Utils.getCurrentWeek(),
                        success = true,
                        locationId = location.id,
                        restaurantId = restaurant.id,
                        document = if (saveDocument) cleanedDocs else null,
                        documentHash = documentHash,
                        scrapeTimestamp = Clock.System.now(),
                        extractionResult = extractionResult,
                    )

                scrapeResultRepository.saveResult(scrapeResult)
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

                scrapeResultRepository.saveResult(scrapeResult)
                throw e
            }
        } catch (e: Exception) {
            logger.error(e) { "Exception thrown while trying to scrape menus for ${location.id}/${restaurant.id}" }
        }
    }

    private fun validateExtractionResult(extractionResult: MenuExtractionResult): MenuExtractionResult {
        if (extractionResult.errors.isNotEmpty()) {
            throw IllegalStateException("Extraction failed. Model returned errors: ${extractionResult.errors}")
        }
        if (extractionResult.lunchMenus == null) {
            throw IllegalStateException("Extraction failed. Model did not provide explanation")
        }
        // Since structured output's JSON schema does not (yet) support string format, we'll have to validate the start and end times.
        // https://platform.openai.com/docs/guides/structured-outputs/some-type-specific-keywords-are-not-yet-supported
        return extractionResult.copy(
            lunchMenus =
                extractionResult.lunchMenus.let { lunchMenus ->
                    lunchMenus.copy(
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
                    )
                },
        )
    }
}
