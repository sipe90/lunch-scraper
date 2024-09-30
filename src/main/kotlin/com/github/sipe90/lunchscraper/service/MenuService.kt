package com.github.sipe90.lunchscraper.service

import com.github.sipe90.lunchscraper.domain.MenuScrapeResult
import com.github.sipe90.lunchscraper.repository.MenuRepository
import com.github.sipe90.lunchscraper.util.Utils
import org.springframework.stereotype.Service

@Service
class MenuService(
    private val menuRepository: MenuRepository,
) {
    fun getAllMenus(locationId: String): List<MenuScrapeResult> =
        menuRepository.loadAllMenus(Utils.getCurrentYear(), Utils.getCurrentWeek(), locationId)

    fun getMenus(
        locationId: String,
        restaurantId: String,
    ): MenuScrapeResult? = menuRepository.loadMenus(Utils.getCurrentYear(), Utils.getCurrentWeek(), locationId, restaurantId)

    fun saveMenus(scrapeResult: MenuScrapeResult) {
        menuRepository.saveMenus(scrapeResult)
    }
}
