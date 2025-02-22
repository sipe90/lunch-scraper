package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.domain.settings.OpenAiSettings
import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.domain.settings.Settings
import org.springframework.stereotype.Service

@Service
class SettingsService(
    private val settingsRepository: SettingsRepository,
) {
    private companion object {
        const val DEFAULT_SYSTEM_PROMPT =
            "Process HTML documents of a restaurant's website and extract the weekly " +
                "lunch menus into a JSON response in the same language that was used in the " +
                "website. Only extract data of the menus for the specified week. Ignore " +
                "menus for all other weeks. Follow instructions in schema description " +
                "properties. If no valid menu for this week is found, output an appropriate " +
                "error message."

        const val DEFAULT_USER_PROMPT_PREFIX =
            "Extract information from the following cleaned HTML document and focus on " +
                "menus for the current week of {{week}}. If the document contains menus for " +
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
            openAi =
                OpenAiSettings(
                    baseUrl = "https://api.openai.com/v1/",
                    model = "gpt-4o",
                    apiKey = "UNSET",
                ),
            scrape =
                ScrapeSettings(
                    enabled = true,
                    schedule = "0 0 8-15 ? * * *",
                    systemPrompt = DEFAULT_SYSTEM_PROMPT,
                    userPromptPrefix = DEFAULT_USER_PROMPT_PREFIX,
                ),
        )
}
