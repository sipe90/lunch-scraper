package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.settings.GlobalSettings
import com.github.sipe90.lunchscraper.domain.settings.OpenAiSettings
import com.github.sipe90.lunchscraper.domain.settings.ScrapeSettings
import com.github.sipe90.lunchscraper.openai.model.Model
import kotlinx.serialization.Serializable

@Serializable
data class GlobalSettingsInput(
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
        val systemPrompt: String,
        val userPromptPrefix: String,
    )

    fun toDomain(): GlobalSettings =
        GlobalSettings(
            openAi =
                OpenAiSettings(
                    baseUrl = openAi.baseUrl,
                    model = Model.fromString(openAi.model),
                    apiKey = openAi.apiKey,
                ),
            scrape =
                ScrapeSettings(
                    systemPrompt = scrape.systemPrompt,
                    userPromptPrefix = scrape.userPromptPrefix,
                ),
        )
}
