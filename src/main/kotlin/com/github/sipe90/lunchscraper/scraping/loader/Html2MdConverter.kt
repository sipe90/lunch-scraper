package com.github.sipe90.lunchscraper.scraping.loader

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import kotlinx.coroutines.coroutineScope

/**
 * https://github.com/vsch/flexmark-java/wiki/Extensions#html-to-markdown
 */
object Html2MdConverter {
    private val converter = FlexmarkHtmlConverter.builder().build()

    suspend fun convertHtmlToMarkdown(html: String): String =
        coroutineScope {
            converter.convert(html, -1)
        }
}
