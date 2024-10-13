package com.github.sipe90.lunchscraper.repository

import com.github.sipe90.lunchscraper.domain.area.Area
import com.github.sipe90.lunchscraper.domain.area.Restaurant
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonValue
import org.springframework.stereotype.Repository

@Repository
class AreaRepositoryImpl(
    private val database: MongoDatabase,
) : AreaRepository {
    private companion object {
        const val COLLECTION = "areas"
    }

    override suspend fun insertOneArea(area: Area): BsonValue? = collection().insertOne(area).insertedId

    override suspend fun deleteAreaById(areaId: String): Long = collection().deleteOne(Filters.eq("_id", areaId)).deletedCount

    override suspend fun findAreaById(areaId: String): Area? = collection().find(Filters.eq("_id", areaId)).firstOrNull()

    override suspend fun findAllAreas(): Flow<Area> = collection().find()

    override suspend fun updateOneArea(
        areaId: String,
        name: String,
    ): Long {
        val filter = Filters.eq("_id", areaId)
        val updates = Updates.set(Area::name.name, name)

        return collection().updateOne(filter, updates).matchedCount
    }

    override suspend fun addRestaurantToArea(
        areaId: String,
        restaurant: Restaurant,
    ): Boolean {
        val filter =
            Filters.and(
                Filters.eq("_id", areaId),
                Filters.not(Filters.elemMatch("restaurants", Filters.eq("_id", restaurant.id))),
            )
        val update = Updates.addToSet(Area::restaurants.name, restaurant)

        return collection().updateOne(filter, update).modifiedCount > 0
    }

    override suspend fun deleteRestaurantFromArea(
        areaId: String,
        restaurantId: String,
    ): Boolean {
        val filter = Filters.eq("_id", areaId)
        val update = Updates.pull(Area::restaurants.name, Filters.eq("_id", restaurantId))

        return collection().updateOne(filter, update).modifiedCount > 0
    }

    override suspend fun updateAreaRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ): Boolean {
        val filter = Filters.eq("_id", areaId)
        val update = Updates.set("restaurants.\$[restaurant]", restaurant)
        val options = UpdateOptions().arrayFilters(listOf(Filters.eq("restaurant._id", restaurant.id)))

        return collection().updateOne(filter, update, options).modifiedCount > 0
    }

    private fun collection() = database.getCollection<Area>(COLLECTION)
}
