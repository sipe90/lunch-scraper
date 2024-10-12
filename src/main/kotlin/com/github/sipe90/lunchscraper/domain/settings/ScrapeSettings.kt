package com.github.sipe90.lunchscraper.domain.settings

import kotlinx.serialization.Serializable

@Serializable
data class ScrapeSettings(
    val systemPrompt: String,
    val userPromptPrefix: String,
)
