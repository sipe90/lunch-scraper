package com.github.sipe90.lunchscraper.luncharea

import com.github.sipe90.lunchscraper.domain.area.LunchArea
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class LunchAreaService(
    private val lunchAreaRepository: LunchAreaRepository,
) {
    suspend fun getAllLunchAreas(): Flow<LunchArea> = lunchAreaRepository.findAllLunchAreas()

    suspend fun getArea(areaId: String): LunchArea? = lunchAreaRepository.findLunchAreaById(areaId)

    suspend fun createLunchArea(lunchArea: LunchArea) {
        logger.info { "Creating a new lunch area: $lunchArea" }

        lunchAreaRepository.insertOneLunchArea(lunchArea)
    }

    suspend fun updateLunchArea(lunchArea: LunchArea) {
        logger.info { "Updating lunch area: $lunchArea" }

        lunchAreaRepository.updateOneLunchArea(lunchArea.id, lunchArea.name)
    }

    suspend fun deleteLunchArea(areaId: String) {
        logger.info { "Deleting lunch area with id: $areaId" }

        lunchAreaRepository.deleteLunchAreaById(areaId)
    }

    suspend fun addRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ) {
        logger.info { "Adding a new restaurant to area with id $areaId: $restaurant" }

        lunchAreaRepository.addRestaurantToLunchArea(areaId, restaurant)
    }

    suspend fun deleteRestaurant(
        areaId: String,
        restaurantId: String,
    ) {
        logger.info { "Deleting a restaurant from area with id $areaId: $restaurantId" }

        lunchAreaRepository.deleteRestaurantFromLunchArea(areaId, restaurantId)
    }

    suspend fun updateRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ) {
        logger.info { "Updating restaurant to area with id $areaId: $restaurant" }

        lunchAreaRepository.updateLunchAreaRestaurant(areaId, restaurant)
    }
}
