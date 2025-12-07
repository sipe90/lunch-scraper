package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.util.Utils.getCurrentWeek
import com.github.sipe90.lunchscraper.util.Utils.getIsoDate

data class ScrapeParameters(
    val name: String,
    val hint: String = "",
    val isoDate: String = getIsoDate(),
    val week: String = getCurrentWeek().toString(),
)
