package com.github.sipe90.lunchscraper.domain.settings

import kotlinx.serialization.Serializable

@Serializable
data class GlobalSettings(
    val openAi: OpenAiSettings,
    val scrape: ScrapeSettings,
)
