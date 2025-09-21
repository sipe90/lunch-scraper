package com.github.sipe90.lunchscraper.scraping.loader

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class Html2MdConverterTest {
    @Test
    fun `should convert html to markdown`() =
        runTest {
            val md = Html2MdConverter.convertHtmlToMarkdown("<div><b>Hello</b></div>")
            assertEquals("**Hello**", md)
        }
}
