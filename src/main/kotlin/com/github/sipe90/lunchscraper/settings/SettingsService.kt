package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.config.SettingsConfig
import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.domain.settings.Settings
import com.github.sipe90.lunchscraper.tasks.ScrapeScheduler
import io.ktor.server.plugins.di.annotations.Property

class SettingsService(
    @Property("lunch-scraper.settings")
    private val settingsConfig: SettingsConfig,
    private val settingsRepository: SettingsRepository,
    private val scrapeScheduler: ScrapeScheduler,
) {
    suspend fun getSettings(): Settings {
        val settings =
            settingsRepository.getSettings()
                ?: return generateDefaultSettings()
                    .also { updateSettings(it) }
        return settings
    }

    suspend fun updateSettings(settings: Settings) {
        val savedSettings = getSettings()
        if (savedSettings == settings) {
            return
        }

        if (settings.scrape != savedSettings.scrape) {
            if (settings.scrape.enabled) {
                scrapeScheduler.startOrResume(settings.scrape.schedule)
            } else {
                scrapeScheduler.pause()
            }
        }

        settingsRepository.upsertSettings(settings)
    }

    private fun generateDefaultSettings(): Settings =
        Settings(
            scrape =
                ScrapeSettings(
                    enabled = true,
                    schedule = settingsConfig.defaultSchedule,
                ),
        )
}
