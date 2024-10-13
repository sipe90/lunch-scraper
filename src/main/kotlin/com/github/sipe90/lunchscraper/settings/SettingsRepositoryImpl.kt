package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.domain.settings.GlobalSettings
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.springframework.stereotype.Repository

@Repository
class SettingsRepositoryImpl(
    private val database: MongoDatabase,
) : SettingsRepository {
    private companion object {
        const val COLLECTION = "settings"
        const val ID = "global-settings"
    }

    override suspend fun getGlobalSettings(): GlobalSettings? =
        collection()
            .find(Filters.eq("_id", ID))
            .firstOrNull()

    override suspend fun upsertGlobalSettings(settings: GlobalSettings) {
        val filter = Filters.eq("_id", ID)
        val updates =
            Updates.combine(
                Updates.set(GlobalSettings::openAi.name, settings.openAi),
                Updates.set(GlobalSettings::scrape.name, settings.scrape),
            )
        val options = UpdateOptions().upsert(true)

        collection().updateOne(filter, updates, options)
    }

    private fun collection() = database.getCollection<GlobalSettings>(COLLECTION)
}
