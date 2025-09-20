package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.LunchAreaInput
import com.github.sipe90.lunchscraper.api.dto.LunchAreaOutput
import com.github.sipe90.lunchscraper.api.dto.LunchAreaUpdate
import com.github.sipe90.lunchscraper.api.dto.RestaurantInput
import com.github.sipe90.lunchscraper.api.dto.RestaurantUpdate
import com.github.sipe90.lunchscraper.luncharea.LunchAreaService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LunchAreaApi(
    private val lunchAreaService: LunchAreaService,
) {
    suspend fun createLunchArea(area: LunchAreaInput) {
        lunchAreaService.createLunchArea(area.toDomain())
    }

    suspend fun getLunchArea(areaId: String): LunchAreaOutput? = lunchAreaService.getArea(areaId)?.toDto()

    suspend fun updateLunchArea(
        areaId: String,
        area: LunchAreaUpdate,
    ) {
        lunchAreaService.updateLunchArea(area.toDomain(areaId))
    }

    suspend fun getAllLunchAreas(): Flow<LunchAreaOutput> = lunchAreaService.getAllLunchAreas().map { it.toDto() }

    suspend fun deleteLunchArea(areaId: String) {
        lunchAreaService.deleteLunchArea(areaId)
    }

    suspend fun addRestaurant(
        areaId: String,
        restaurant: RestaurantInput,
    ) {
        lunchAreaService.addRestaurant(areaId, restaurant.toDomain())
    }

    suspend fun updateRestaurant(
        areaId: String,
        restaurantId: String,
        restaurant: RestaurantUpdate,
    ) {
        lunchAreaService.updateRestaurant(areaId, restaurant.toDomain(restaurantId))
    }

    suspend fun deleteRestaurant(
        areaId: String,
        restaurantId: String,
    ) {
        lunchAreaService.deleteRestaurant(areaId, restaurantId)
    }
}
