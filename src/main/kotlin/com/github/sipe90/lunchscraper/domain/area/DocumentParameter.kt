package com.github.sipe90.lunchscraper.domain.area

import kotlinx.serialization.Serializable

@Serializable
data class DocumentParameter(
    val url: String,
    val cssSelector: String? = null,
)
