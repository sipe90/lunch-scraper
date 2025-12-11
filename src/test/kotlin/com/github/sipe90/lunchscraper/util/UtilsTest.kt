package com.github.sipe90.lunchscraper.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UtilsTest {
    @Nested
    inner class `replacePlaceholders- function` {
        @Test
        fun `should replace all placeholders`() {
            val testText = "{{a}} and {{b}} and {{c}}"
            val params = mapOf("a" to "a", "b" to "b", "c" to "c")

            val result = Utils.replacePlaceholders(testText, params)

            assertEquals("a and b and c", result)
        }

        @Test
        fun `should throw an exception if parameter not found`() {
            val testText = "{{a}} and {{b}} and {{c}}"
            val params = mapOf("a" to "a")

            val exception: IllegalArgumentException = assertThrows { Utils.replacePlaceholders(testText, params) }

            assertEquals("Text contains undefined variables: b, c", exception.message)
        }
    }
}
