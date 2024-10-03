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
class MenuRepository {
    private val json = Json(DefaultJson) {
        prettyPrint = true
    }

    private val saveFolder = "data"

    init {
        createDirs()
    }

    fun saveMenus(scrapeResult: MenuScrapeResult) {
        createDirs(scrapeResult.year, scrapeResult.week, scrapeResult.locationId)

        val menusJson = json.encodeToString(scrapeResult)

        val filePath = getPath(scrapeResult.year, scrapeResult.week, scrapeResult.locationId, "${scrapeResult.restaurantId}.json")
        Files.writeString(filePath, menusJson)
    }

    fun loadMenus(
        year: Int = Utils.getCurrentYear(),
        week: Int = Utils.getCurrentWeek(),
        locationId: String,
        restaurantId: String,
    ): MenuScrapeResult? {
        val filePath = getPath(year, week, locationId, "$restaurantId.json")
        if (!Files.exists(filePath)) {
            return null
        }

        val menusString = Files.readString(filePath)
        return json.decodeFromString(menusString)
    }

    fun loadAllMenus(
        year: Int = Utils.getCurrentYear(),
        week: Int = Utils.getCurrentWeek(),
        locationId: String,
    ): List<MenuScrapeResult> {
        createDirs(year, week, locationId)
        return Files
            .list(getPath(year, week, locationId))
            .map {
                val menusString = Files.readString(it)
                json.decodeFromString<MenuScrapeResult>(menusString)
            }.toList()
    }

    private fun createDirs(
        year: Int? = null,
        week: Int? = null,
        location: String? = null,
    ) = Files.createDirectories(getPath(year, week, location))

    private fun getPath(
        year: Int? = null,
        week: Int? = null,
        location: String? = null,
        fileName: String? = null,
    ) = when {
        year != null && week != null && location != null && fileName != null -> Paths.get(saveFolder, "${year}_$week", location, fileName)
        year != null && week != null && location != null -> Paths.get(saveFolder, "${year}_$week", location)
        year != null && week != null -> Paths.get(saveFolder, "${year}_$week")
        else -> Paths.get(saveFolder)
    }
}
