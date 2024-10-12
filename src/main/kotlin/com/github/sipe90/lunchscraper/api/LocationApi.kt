package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.LocationInput
import com.github.sipe90.lunchscraper.api.dto.LocationOutput
import com.github.sipe90.lunchscraper.api.dto.LocationUpdate
import com.github.sipe90.lunchscraper.api.dto.RestaurantInput
import com.github.sipe90.lunchscraper.api.dto.RestaurantUpdate
import com.github.sipe90.lunchscraper.settings.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class LocationApi(
    private val locationService: LocationService,
) {
    suspend fun createLocation(location: LocationInput) {
        locationService.createLocation(location.toDomain())
    }

    suspend fun getLocation(locationId: String): LocationOutput? = locationService.getLocation(locationId)?.toDto()

    suspend fun updateLocation(locationId: String, location: LocationUpdate) {
        locationService.updateLocation(location.toDomain(locationId))
    }

    suspend fun getAllLocations(): Flow<LocationOutput> = locationService.getAllLocations().map { it.toDto() }

    suspend fun deleteLocation(locationId: String) {
        locationService.deleteLocation(locationId)
    }

    suspend fun addRestaurant(locationId: String, restaurant: RestaurantInput) {
        locationService.addRestaurant(locationId, restaurant.toDomain())
    }

    suspend fun updateRestaurant(locationId: String, restaurantId: String, restaurant: RestaurantUpdate) {
        locationService.updateRestaurant(locationId, restaurant.toDomain(restaurantId))
    }

    suspend fun deleteRestaurant(locationId: String, restaurantId: String) {
        locationService.deleteRestaurant(locationId, restaurantId)
    }
}
