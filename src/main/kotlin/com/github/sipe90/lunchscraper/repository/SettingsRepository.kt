package com.github.sipe90.lunchscraper.repository

import com.github.sipe90.lunchscraper.domain.settings.GlobalSettings

interface SettingsRepository {
    suspend fun getGlobalSettings(): GlobalSettings?

    suspend fun upsertGlobalSettings(settings: GlobalSettings)
}
