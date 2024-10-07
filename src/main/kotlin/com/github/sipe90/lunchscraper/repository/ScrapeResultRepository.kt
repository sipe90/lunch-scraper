package com.github.sipe90.lunchscraper.repository

import com.github.sipe90.lunchscraper.domain.MenuScrapeResult
import com.github.sipe90.lunchscraper.util.Utils
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Temporary local filesystem implementation.
 */
@Repository
class ScrapeResultRepository {
    private val json =
        Json(DefaultJson) {
            prettyPrint = true
        }

    private val saveFolder = "data"

    init {
        createDirs(true)
        createDirs(false)
    }

    fun saveResult(scrapeResult: MenuScrapeResult) {
        createDirs(scrapeResult.success, scrapeResult.year, scrapeResult.week, scrapeResult.locationId)

        val menusJson = json.encodeToString(scrapeResult)

        val filePath =
            getPath(
                scrapeResult.success,
                scrapeResult.year,
                scrapeResult.week,
                scrapeResult.locationId,
                "${scrapeResult.restaurantId}.json",
            )
        Files.writeString(filePath, menusJson)
    }

    fun loadResult(
        year: Int = Utils.getCurrentYear(),
        week: Int = Utils.getCurrentWeek(),
        locationId: String,
        restaurantId: String,
    ): MenuScrapeResult? {
        val filePath = getPath(true, year, week, locationId, "$restaurantId.json")
        if (!Files.exists(filePath)) {
            return null
        }

        val menusString = Files.readString(filePath)
        return json.decodeFromString(menusString)
    }

    fun loadAllResults(
        year: Int = Utils.getCurrentYear(),
        week: Int = Utils.getCurrentWeek(),
        locationId: String,
    ): List<MenuScrapeResult> {
        createDirs(true, year, week, locationId)
        return Files
            .list(getPath(true, year, week, locationId))
            .map {
                val menusString = Files.readString(it)
                json.decodeFromString<MenuScrapeResult>(menusString)
            }.toList()
    }

    private fun createDirs(
        success: Boolean,
        year: Int? = null,
        week: Int? = null,
        location: String? = null,
    ) = Files.createDirectories(getPath(success, year, week, location))

    private fun getPath(
        success: Boolean,
        year: Int? = null,
        week: Int? = null,
        location: String? = null,
        fileName: String? = null,
    ) = if (success) {
        when {
            year != null && week != null && location != null && fileName != null ->
                Paths.get(
                    saveFolder,
                    "results",
                    "${year}_$week",
                    location,
                    fileName,
                )
            year != null && week != null && location != null -> Paths.get(saveFolder, "results", "${year}_$week", location)
            year != null && week != null -> Paths.get(saveFolder, "results", "${year}_$week")
            else -> Paths.get(saveFolder, "results")
        }
    } else {
        when {
            year != null && week != null && location != null && fileName != null ->
                Paths.get(
                    saveFolder,
                    "failed",
                    "${year}_$week",
                    location,
                    fileName,
                )
            year != null && week != null && location != null -> Paths.get(saveFolder, "failed", "${year}_$week", location)
            year != null && week != null -> Paths.get(saveFolder, "failed", "${year}_$week")
            else -> Paths.get(saveFolder, "failed")
        }
    }
}
