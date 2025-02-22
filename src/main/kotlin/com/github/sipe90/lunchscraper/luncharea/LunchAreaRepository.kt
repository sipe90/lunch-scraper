package com.github.sipe90.lunchscraper.luncharea

import com.github.sipe90.lunchscraper.domain.area.LunchArea
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import kotlinx.coroutines.flow.Flow
import org.bson.BsonValue

interface LunchAreaRepository {
    suspend fun insertOneLunchArea(lunchArea: LunchArea): BsonValue?

    suspend fun deleteLunchAreaById(areaId: String): Long

    suspend fun findLunchAreaById(areaId: String): LunchArea?

    suspend fun findAllLunchAreas(): Flow<LunchArea>

    suspend fun updateOneLunchArea(
        areaId: String,
        name: String,
    ): Long

    suspend fun addRestaurantToLunchArea(
        areaId: String,
        restaurant: Restaurant,
    ): Boolean

    suspend fun deleteRestaurantFromLunchArea(
        areaId: String,
        restaurantId: String,
    ): Boolean

    suspend fun updateLunchAreaRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ): Boolean
}
