package com.github.sipe90.lunchscraper.domain.settings

import com.github.sipe90.lunchscraper.openai.model.Model
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiSettings(
    val baseUrl: String,
    val model: Model,
    val apiKey: String,
)
