package com.github.sipe90.lunchscraper.util

import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoField

object Utils {
    fun getCurrentYear(): Int = Year.now().value

    fun getCurrentWeek() = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR)

    fun replacePlaceholders(
        prompt: String,
        params: Map<String, String>,
    ): String {
        val p = params.entries.fold(prompt) { p, (variable, value) -> p.replace("{{$variable}}", value) }
        if (p.contains(Regex.fromLiteral("{{\\w*}}"))) {
            throw IllegalArgumentException("Prompt contains undefined variables: $p")
        }
        return p
    }
}
