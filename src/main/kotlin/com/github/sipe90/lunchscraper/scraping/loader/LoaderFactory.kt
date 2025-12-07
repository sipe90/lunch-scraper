package com.github.sipe90.lunchscraper.scraping.loader

import com.github.sipe90.lunchscraper.domain.area.HtmlScrapeParameters
import com.github.sipe90.lunchscraper.domain.area.JsonScrapeParameters
import com.github.sipe90.lunchscraper.domain.area.ScrapeParameters

object LoaderFactory {
    fun create(scrapeParams: ScrapeParameters): Loader =
        when (scrapeParams) {
            is HtmlScrapeParameters -> HtmlLoader(scrapeParams)
            is JsonScrapeParameters -> JsonLoader(scrapeParams)
        }
}
