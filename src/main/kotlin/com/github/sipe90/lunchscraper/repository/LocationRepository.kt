package com.github.sipe90.lunchscraper.repository

import com.github.sipe90.lunchscraper.domain.location.Location
import com.github.sipe90.lunchscraper.domain.location.Restaurant
import kotlinx.coroutines.flow.Flow
import org.bson.BsonValue

interface LocationRepository {
    suspend fun insertOneLocation(settings: Location): BsonValue?

    suspend fun deleteLocationById(locationId: String): Long

    suspend fun findLocationById(locationId: String): Location?

    suspend fun findAllLocations(): Flow<Location>

    suspend fun updateOneLocation(
        locationId: String,
        name: String,
    ): Long

    suspend fun addRestaurantToLocation(
        locationId: String,
        restaurant: Restaurant,
    ): Boolean

    suspend fun deleteRestaurantFromLocation(
        locationId: String,
        restaurantId: String,
    ): Boolean

    suspend fun updateLocationRestaurant(
        locationId: String,
        restaurant: Restaurant,
    ): Boolean
}
