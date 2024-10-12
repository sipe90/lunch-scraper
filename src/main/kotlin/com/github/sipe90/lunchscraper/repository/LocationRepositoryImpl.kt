package com.github.sipe90.lunchscraper.repository

import com.github.sipe90.lunchscraper.domain.location.Location
import com.github.sipe90.lunchscraper.domain.location.Restaurant
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonValue
import org.springframework.stereotype.Repository

@Repository
class LocationRepositoryImpl(
    private val database: MongoDatabase,
) : LocationRepository {
    private companion object {
        const val COLLECTION = "locations"
    }

    override suspend fun insertOneLocation(settings: Location): BsonValue? = collection().insertOne(settings).insertedId

    override suspend fun deleteLocationById(locationId: String): Long = collection().deleteOne(Filters.eq("_id", locationId)).deletedCount

    override suspend fun findLocationById(locationId: String): Location? = collection().find(Filters.eq("_id", locationId)).firstOrNull()

    override suspend fun findAllLocations(): Flow<Location> = collection().find()

    override suspend fun updateOneLocation(
        locationId: String,
        name: String,
    ): Long {
        val filter = Filters.eq("_id", locationId)
        val updates = Updates.set(Location::name.name, name)

        return collection().updateOne(filter, updates).matchedCount
    }

    override suspend fun addRestaurantToLocation(
        locationId: String,
        restaurant: Restaurant,
    ): Boolean {
        val filter =
            Filters.and(
                Filters.eq("_id", locationId),
                Filters.not(Filters.elemMatch("restaurants", Filters.eq("_id", restaurant.id))),
            )
        val update = Updates.addToSet(Location::restaurants.name, restaurant)

        return collection().updateOne(filter, update).modifiedCount > 0
    }

    override suspend fun deleteRestaurantFromLocation(
        locationId: String,
        restaurantId: String,
    ): Boolean {
        val filter = Filters.eq("_id", locationId)
        val update = Updates.pull(Location::restaurants.name, Filters.eq("_id", restaurantId))

        return collection().updateOne(filter, update).modifiedCount > 0
    }

    override suspend fun updateLocationRestaurant(
        locationId: String,
        restaurant: Restaurant,
    ): Boolean {
        val filter = Filters.eq("_id", locationId)
        val update = Updates.set("restaurants.\$[restaurant]", restaurant)
        val options = UpdateOptions().arrayFilters(listOf(Filters.eq("restaurant._id", restaurant.id)))

        return collection().updateOne(filter, update, options).modifiedCount > 0
    }

    private fun collection() = database.getCollection<Location>(COLLECTION)
}
