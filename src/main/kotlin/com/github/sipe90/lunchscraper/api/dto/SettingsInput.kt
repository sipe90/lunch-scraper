package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.settings.OpenAiSettings
import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.domain.settings.Settings
import kotlinx.serialization.Serializable

@Serializable
data class SettingsInput(
    val openAi: OpenAiSettingsInput,
    val scrape: ScrapeSettingsInput,
) {
    @Serializable
    data class OpenAiSettingsInput(
        val baseUrl: String,
        val model: String,
        val apiKey: String,
    )

    @Serializable
    data class ScrapeSettingsInput(
        val enabled: Boolean,
        val schedule: String,
        val systemPrompt: String,
        val userPromptPrefix: String,
    )

    fun toDomain(): Settings =
        Settings(
            openAi =
                OpenAiSettings(
                    baseUrl = openAi.baseUrl,
                    model = openAi.model,
                    apiKey = openAi.apiKey,
                ),
            scrape =
                ScrapeSettings(
                    enabled = scrape.enabled,
                    schedule = scrape.schedule,
                    systemPrompt = scrape.systemPrompt,
                    userPromptPrefix = scrape.userPromptPrefix,
                ),
        )
}
