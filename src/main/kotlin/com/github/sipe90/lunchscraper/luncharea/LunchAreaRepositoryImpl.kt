package com.github.sipe90.lunchscraper.luncharea

import com.github.sipe90.lunchscraper.domain.area.LunchArea
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
class LunchAreaRepositoryImpl(
    private val database: MongoDatabase,
) : LunchAreaRepository {
    private companion object {
        const val COLLECTION = "lunch-areas"
    }

    override suspend fun insertOneLunchArea(lunchArea: LunchArea): BsonValue? = collection().insertOne(lunchArea).insertedId

    override suspend fun deleteLunchAreaById(areaId: String): Long = collection().deleteOne(Filters.eq("_id", areaId)).deletedCount

    override suspend fun findLunchAreaById(areaId: String): LunchArea? = collection().find(Filters.eq("_id", areaId)).firstOrNull()

    override suspend fun findAllLunchAreas(): Flow<LunchArea> = collection().find()

    override suspend fun updateOneLunchArea(
        areaId: String,
        name: String,
    ): Long {
        val filter = Filters.eq("_id", areaId)
        val updates = Updates.set(LunchArea::name.name, name)

        return collection().updateOne(filter, updates).matchedCount
    }

    override suspend fun addRestaurantToLunchArea(
        areaId: String,
        restaurant: Restaurant,
    ): Boolean {
        val filter =
            Filters.and(
                Filters.eq("_id", areaId),
                Filters.not(Filters.elemMatch("restaurants", Filters.eq("_id", restaurant.id))),
            )
        val update = Updates.addToSet(LunchArea::restaurants.name, restaurant)

        return collection().updateOne(filter, update).modifiedCount > 0
    }

    override suspend fun deleteRestaurantFromLunchArea(
        areaId: String,
        restaurantId: String,
    ): Boolean {
        val filter = Filters.eq("_id", areaId)
        val update = Updates.pull(LunchArea::restaurants.name, Filters.eq("_id", restaurantId))

        return collection().updateOne(filter, update).modifiedCount > 0
    }

    override suspend fun updateLunchAreaRestaurant(
        areaId: String,
        restaurant: Restaurant,
    ): Boolean {
        val filter = Filters.eq("_id", areaId)
        val update = Updates.set("restaurants.\$[restaurant]", restaurant)
        val options = UpdateOptions().arrayFilters(listOf(Filters.eq("restaurant._id", restaurant.id)))

        return collection().updateOne(filter, update, options).modifiedCount > 0
    }

    private fun collection() = database.getCollection<LunchArea>(COLLECTION)
}
