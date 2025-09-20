package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.SettingsInput
import com.github.sipe90.lunchscraper.domain.settings.Settings
import com.github.sipe90.lunchscraper.settings.SettingsService

class SettingsApi(
    private val settingsService: SettingsService,
) {
    suspend fun getSettings(): Settings = settingsService.getSettings()

    suspend fun updateSettings(settings: SettingsInput) = settingsService.updateSettings(settings.toDomain())
}
