package com.github.sipe90.lunchscraper.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiConfig(
    @SerialName("api-key")
    val apiKey: String,
)

@Serializable
data class MongoDbConfig(
    val url: String,
    val database: String,
)

@Serializable
data class OpenAiConfig(
    @SerialName("api-key")
    val apiKey: String,
    @SerialName("base-url")
    val baseUrl: String,
    val model: String,
)

@Serializable
data class SettingsConfig(
    @SerialName("default-schedule")
    val defaultSchedule: String,
    @SerialName("default-system-prompt")
    val defaultSystemPrompt: String,
    @SerialName("default-user-prompt-prefix")
    val defaultUserPromptPrefix: String,
)
