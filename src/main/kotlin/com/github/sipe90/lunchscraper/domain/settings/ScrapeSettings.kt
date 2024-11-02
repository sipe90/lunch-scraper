package com.github.sipe90.lunchscraper.domain.settings

import kotlinx.serialization.Serializable

@Serializable
data class ScrapeSettings(
    val enabled: Boolean,
    val schedule: String,
    val systemPrompt: String,
    val userPromptPrefix: String,
)
