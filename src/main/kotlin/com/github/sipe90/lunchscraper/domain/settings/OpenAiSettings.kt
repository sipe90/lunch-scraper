package com.github.sipe90.lunchscraper.domain.settings

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiSettings(
    val baseUrl: String,
    val model: String,
    val apiKey: String,
)
