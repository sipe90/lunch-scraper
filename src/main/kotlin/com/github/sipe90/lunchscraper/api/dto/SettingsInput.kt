package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.domain.settings.Settings
import kotlinx.serialization.Serializable

@Serializable
data class SettingsInput(
    val scrape: ScrapeSettingsInput,
) {
    @Serializable
    data class ScrapeSettingsInput(
        val enabled: Boolean,
        val schedule: String,
    )

    fun toDomain(): Settings =
        Settings(
            scrape =
                ScrapeSettings(
                    enabled = scrape.enabled,
                    schedule = scrape.schedule,
                ),
        )
}
