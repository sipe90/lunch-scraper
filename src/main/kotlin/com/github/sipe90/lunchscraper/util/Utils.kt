package com.github.sipe90.lunchscraper.util

import java.io.IOException
import java.time.LocalDate
import java.time.Year
import java.time.temporal.ChronoField

object Utils {
    fun readFileToString(path: String): String =
        javaClass.getResourceAsStream(path)?.use {
            it.readAllBytes().decodeToString()
        } ?: throw IOException("Unable to read file from $path.")

    fun getCurrentYear(): Int = Year.now().value

    fun getCurrentWeek(): Int = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR)

    fun getIsoDate(): String = LocalDate.now().toString()

    /**
     * Finds any placeholders in the input text and tries to replace them with values from the parameters map.
     * Placeholders are expected to be in format {{placeholderParmName}}.
     * Exception is thrown if any placeholder values are missing from the map.
     */
    fun replacePlaceholders(
        text: String,
        params: Map<String, Any?>,
    ): String {
        // Matches things like {{foo}}, capturing `foo` as group 1
        val placeholderRegex = "\\{\\{(\\w+)}}".toRegex()

        val missing = mutableSetOf<String>()

        val result =
            placeholderRegex.replace(text) { matchResult ->
                val variableName = matchResult.groupValues[1]
                val value = params[variableName]

                if (value == null) {
                    missing += variableName
                    matchResult.value
                } else {
                    value.toString()
                }
            }

        if (missing.isNotEmpty()) {
            throw IllegalArgumentException(
                "Text contains undefined variables: ${missing.joinToString(", ")}",
            )
        }

        return result
    }
}
