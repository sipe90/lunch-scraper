package com.github.sipe90.lunchscraper.scraping.loader

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Html2MdConverterTest {
    @Test
    fun `should convert html to markdown`() =
        runTest {
            val md = Html2MdConverter.convertHtmlToMarkdown("<div><b>Hello</b></div>")
            assertEquals("**Hello**", md)
        }
}
