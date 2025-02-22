package com.github.sipe90.lunchscraper.serialization

import com.github.sipe90.lunchscraper.domain.area.HtmlScrapeParameters
import com.github.sipe90.lunchscraper.domain.area.JsonScrapeParameters
import com.github.sipe90.lunchscraper.domain.area.ScrapeParameters
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ScrapeParametersSerializer : JsonContentPolymorphicSerializer<ScrapeParameters>(ScrapeParameters::class) {
    override fun selectDeserializer(element: JsonElement) =
        when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "html" -> HtmlScrapeParameters.serializer()
            "json" -> JsonScrapeParameters.serializer()
            else -> throw IllegalArgumentException()
        }
}
