package com.github.sipe90.lunchscraper.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AreaOutput(
    val id: String,
    val name: String,
    val restaurants: List<RestaurantOutput>,
)
