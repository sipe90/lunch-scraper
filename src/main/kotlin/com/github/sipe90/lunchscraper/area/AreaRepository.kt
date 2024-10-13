package com.github.sipe90.lunchscraper.area

import com.github.sipe90.lunchscraper.domain.area.Area
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import kotlinx.coroutines.flow.Flow
import org.bson.BsonValue

interface AreaRepository {
    suspend fun insertOneArea(area: Area): BsonValue?

    suspend fun deleteAreaById(areaId: String): Long

    suspend fun findAreaById(areaId: String): Area?

    suspend fun findAllAreas(): Flow<Area>

    suspend fun updateOneArea(
        areaId: String,
        name: String,
    ): Long

    suspend fun addRestaurantToArea(
        areaId: String,
        restaurant: Restaurant,
    ): Boolean

    suspend fun deleteRestaurantFromArea(
        areaId: String,
        restaurantId: String,
    ): Boolean

    suspend fun updateAreaRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ): Boolean
}
