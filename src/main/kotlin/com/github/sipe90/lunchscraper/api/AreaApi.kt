package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.AreaInput
import com.github.sipe90.lunchscraper.api.dto.AreaOutput
import com.github.sipe90.lunchscraper.api.dto.AreaUpdate
import com.github.sipe90.lunchscraper.api.dto.RestaurantInput
import com.github.sipe90.lunchscraper.api.dto.RestaurantUpdate
import com.github.sipe90.lunchscraper.area.AreaService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class AreaApi(
    private val areaService: AreaService,
) {
    suspend fun createArea(area: AreaInput) {
        areaService.createArea(area.toDomain())
    }

    suspend fun getArea(areaId: String): AreaOutput? = areaService.getArea(areaId)?.toDto()

    suspend fun updateArea(
        areaId: String,
        area: AreaUpdate,
    ) {
        areaService.updateArea(area.toDomain(areaId))
    }

    suspend fun getAllAreas(): Flow<AreaOutput> = areaService.getAllAreas().map { it.toDto() }

    suspend fun deleteArea(areaId: String) {
        areaService.deleteArea(areaId)
    }

    suspend fun addRestaurant(
        areaId: String,
        restaurant: RestaurantInput,
    ) {
        areaService.addRestaurant(areaId, restaurant.toDomain())
    }

    suspend fun updateRestaurant(
        areaId: String,
        restaurantId: String,
        restaurant: RestaurantUpdate,
    ) {
        areaService.updateRestaurant(areaId, restaurant.toDomain(restaurantId))
    }

    suspend fun deleteRestaurant(
        areaId: String,
        restaurantId: String,
    ) {
        areaService.deleteRestaurant(areaId, restaurantId)
    }
}
