package com.github.sipe90.lunchscraper.api.dto

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
}
