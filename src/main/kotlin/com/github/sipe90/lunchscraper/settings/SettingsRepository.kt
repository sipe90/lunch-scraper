package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.domain.settings.Settings

interface SettingsRepository {
    suspend fun getSettings(): Settings?

    suspend fun upsertSettings(settings: Settings)
}
