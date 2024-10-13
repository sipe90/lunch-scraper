package com.github.sipe90.lunchscraper.settings

import com.github.sipe90.lunchscraper.domain.settings.GlobalSettings
import com.github.sipe90.lunchscraper.domain.settings.OpenAiSettings
import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.openai.model.Model
import org.springframework.stereotype.Service

@Service
class GlobalSettingsService(
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

    suspend fun getGlobalSettings(): GlobalSettings {
        val settings =
            settingsRepository.getGlobalSettings()
                ?: return generateDefaultGlobalSettings()
                    .also { updateGlobalSettings(it) }
        return settings
    }

    suspend fun updateGlobalSettings(settings: GlobalSettings) {
        settingsRepository.upsertGlobalSettings(settings)
    }

    private fun generateDefaultGlobalSettings(): GlobalSettings =
        GlobalSettings(
            openAi =
                OpenAiSettings(
                    baseUrl = "https://api.openai.com/v1/",
                    model = Model.fromString("gpt-4o"),
                    apiKey = "UNSET",
                ),
            scrape =
                ScrapeSettings(
                    systemPrompt = DEFAULT_SYSTEM_PROMPT,
                    userPromptPrefix = DEFAULT_USER_PROMPT_PREFIX,
                ),
        )
}
