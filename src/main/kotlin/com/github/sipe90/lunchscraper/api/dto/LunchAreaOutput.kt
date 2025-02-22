package com.github.sipe90.lunchscraper.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LunchAreaOutput(
    val id: String,
    val name: String,
)
