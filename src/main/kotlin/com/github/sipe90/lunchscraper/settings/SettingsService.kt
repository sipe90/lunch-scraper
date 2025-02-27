package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.config.SettingsConfig
import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.domain.settings.Settings
import org.springframework.stereotype.Service

@Service
class SettingsService(
    private val settingsConfig: SettingsConfig,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun getSettings(): Settings {
        val settings =
            settingsRepository.getSettings()
                ?: return generateDefaultSettings()
                    .also { updateSettings(it) }
        return settings
    }

    suspend fun updateSettings(settings: Settings) {
        settingsRepository.upsertSettings(settings)
    }

    private fun generateDefaultSettings(): Settings =
        Settings(
            scrape =
                ScrapeSettings(
                    enabled = true,
                    schedule = settingsConfig.defaultSchedule,
                    systemPrompt = settingsConfig.defaultSystemPrompt,
                    userPromptPrefix = settingsConfig.defaultUserPromptPrefix,
                ),
        )
}
