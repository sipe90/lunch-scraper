package com.github.sipe90.lunchscraper.luncharea

import com.github.sipe90.lunchscraper.domain.area.LunchArea
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class LunchAreaService(
    private val lunchAreaRepository: LunchAreaRepository,
) {
    suspend fun getAllLunchAreas(): Flow<LunchArea> = lunchAreaRepository.findAllLunchAreas()

    suspend fun getArea(areaId: String): LunchArea? = lunchAreaRepository.findLunchAreaById(areaId)

    suspend fun createLunchArea(lunchArea: LunchArea) {
        lunchAreaRepository.insertOneLunchArea(lunchArea)
    }

    suspend fun updateLunchArea(lunchArea: LunchArea) {
        lunchAreaRepository.updateOneLunchArea(lunchArea.id, lunchArea.name)
    }

    suspend fun deleteLunchArea(areaId: String) {
        lunchAreaRepository.deleteLunchAreaById(areaId)
    }

    suspend fun addRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ) {
        lunchAreaRepository.addRestaurantToLunchArea(areaId, restaurant)
    }

    suspend fun deleteRestaurant(
        areaId: String,
        restaurantId: String,
    ) {
        lunchAreaRepository.deleteRestaurantFromLunchArea(areaId, restaurantId)
    }

    suspend fun updateRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ) {
        lunchAreaRepository.updateLunchAreaRestaurant(areaId, restaurant)
    }
}
