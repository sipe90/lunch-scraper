package com.github.sipe90.lunchscraper.api

import com.github.sipe90.lunchscraper.api.dto.GlobalSettingsInput
import com.github.sipe90.lunchscraper.domain.settings.GlobalSettings
import com.github.sipe90.lunchscraper.settings.GlobalSettingsService
import org.springframework.stereotype.Service

@Service
class SettingsApi(
    private val settingsService: GlobalSettingsService,
) {
    suspend fun getGlobalSettings(): GlobalSettings = settingsService.getGlobalSettings()

    suspend fun updateGlobalSettings(globalSettings: GlobalSettingsInput) = settingsService.updateGlobalSettings(globalSettings.toDomain())
}
