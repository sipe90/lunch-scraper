package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.domain.location.Location
import com.github.sipe90.lunchscraper.domain.location.Restaurant
import com.github.sipe90.lunchscraper.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class LocationService(
    private val locationRepository: LocationRepository,
) {
    suspend fun getAllLocations(): Flow<Location> = locationRepository.findAllLocations()

    suspend fun getLocation(locationId: String): Location? = locationRepository.findLocationById(locationId)

    suspend fun createLocation(location: Location) {
        locationRepository.insertOneLocation(location)
    }

    suspend fun updateLocation(location: Location) {
        locationRepository.updateOneLocation(location.id, location.name)
    }

    suspend fun deleteLocation(locationId: String) {
        locationRepository.deleteLocationById(locationId)
    }

    suspend fun addRestaurant(
        locationId: String,
        restaurant: Restaurant,
    ) {
        locationRepository.addRestaurantToLocation(locationId, restaurant)
    }

    suspend fun deleteRestaurant(
        locationId: String,
        restaurantId: String,
    ) {
        locationRepository.deleteRestaurantFromLocation(locationId, restaurantId)
    }

    suspend fun updateRestaurant(
        locationId: String,
        restaurant: Restaurant,
    ) {
        locationRepository.updateLocationRestaurant(locationId, restaurant)
    }
}
