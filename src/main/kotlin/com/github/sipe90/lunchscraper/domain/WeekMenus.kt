package com.github.sipe90.lunchscraper.domain

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class WeekMenus(
    val year: Short,
    val week: Short,
    val venues: List<Venue>,
)

@Serializable
data class Venue(
    val name: String,
    val url: String,
    val weeklyMenu: Boolean,
    val buffet: Boolean,
    val buffetPrice: Double? = null,
    val lunchStart: LocalTime? = null,
    val lunchEnd: LocalTime? = null,
    val allWeekMenu: List<MenuItem>? = null,
    val weekMenu: List<MenuItem>? = null,
)

@Serializable
data class MenuItem(
    val name: String,
    val description: String? = null,
    val price: Double,
)
