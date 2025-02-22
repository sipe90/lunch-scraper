package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.domain.settings.Settings
import org.springframework.stereotype.Service

@Service
class SettingsService(
    private val settingsRepository: SettingsRepository,
) {
    private companion object {
        const val DEFAULT_SYSTEM_PROMPT =
            "You act as a data extractor. You process text documents scraped off from a restaurant's website " +
                "and the expected end result is a JSON document containing the current week's lunch menu." +
                "Use the same language as the one used in the documents. " +
                "Only extract data of the menus for the specified week and ignore menus for all other weeks. " +
                "Follow instructions in schema description properties. " +
                "If no valid menu for this week is found, output an appropriate error message. " +
                "You are allowed and encouraged to correct small spelling mistakes in menu item descriptions."

        const val DEFAULT_USER_PROMPT_PREFIX =
            "Extract information from the following text document(s) and focus on " +
                "menus for the current week ({{week}}). If the document contains menus for " +
                "multiple restaurants, only extract menus for the restaurant named {{name}}."
    }

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
                    schedule = "0 0 8-15 ? * * *",
                    systemPrompt = DEFAULT_SYSTEM_PROMPT,
                    userPromptPrefix = DEFAULT_USER_PROMPT_PREFIX,
                ),
        )
}
