package com.github.sipe90.lunchscraper.api.dto

import com.github.sipe90.lunchscraper.domain.area.ScrapeParameters
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantOutput(
    val id: String,
    val name: String,
    val url: String? = null,
    val parameters: ScrapeParameters,
)
