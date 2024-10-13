package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.domain.settings.GlobalSettings

interface SettingsRepository {
    suspend fun getGlobalSettings(): GlobalSettings?

    suspend fun upsertGlobalSettings(settings: GlobalSettings)
}
