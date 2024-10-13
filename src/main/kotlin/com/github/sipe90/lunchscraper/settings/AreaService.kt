package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.domain.area.Area
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import com.github.sipe90.lunchscraper.repository.AreaRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class AreaService(
    private val areaRepository: AreaRepository,
) {
    suspend fun getAllAreas(): Flow<Area> = areaRepository.findAllAreas()

    suspend fun getArea(areaId: String): Area? = areaRepository.findAreaById(areaId)

    suspend fun createArea(area: Area) {
        areaRepository.insertOneArea(area)
    }

    suspend fun updateArea(area: Area) {
        areaRepository.updateOneArea(area.id, area.name)
    }

    suspend fun deleteArea(areaId: String) {
        areaRepository.deleteAreaById(areaId)
    }

    suspend fun addRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ) {
        areaRepository.addRestaurantToArea(areaId, restaurant)
    }

    suspend fun deleteRestaurant(
        areaId: String,
        restaurantId: String,
    ) {
        areaRepository.deleteRestaurantFromArea(areaId, restaurantId)
    }

    suspend fun updateRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ) {
        areaRepository.updateAreaRestaurant(areaId, restaurant)
    }
}
