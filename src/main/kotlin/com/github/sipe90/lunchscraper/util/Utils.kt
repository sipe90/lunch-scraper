package com.github.sipe90.lunchscraper.util

import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoField

object Utils {
    fun getCurrentYear(): Int = Year.now().value

    fun getCurrentWeek(): Int = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR)

    fun getIsoDate(): String = LocalDate.now().toString()

    fun replacePlaceholders(
        text: String,
        params: Map<String, String> = emptyMap(),
    ): String {
        val allParams = params + getTemporalParams()
        val p = allParams.entries.fold(text) { p, (variable, value) -> p.replace("{{$variable}}", value) }
        if (p.contains(Regex.fromLiteral("{{\\w*}}"))) {
            throw IllegalArgumentException("Prompt contains undefined variables: $p")
        }
        return p
    }

    private fun getTemporalParams() =
        mapOf(
            "week" to getCurrentWeek().toString(),
            "isoDate" to getIsoDate(),
        )
}
