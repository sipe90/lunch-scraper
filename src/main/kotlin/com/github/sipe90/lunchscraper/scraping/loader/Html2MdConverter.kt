package com.github.sipe90.lunchscraper.scraping.loader

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.html2md.converter.LinkConversion
import com.vladsch.flexmark.util.data.MutableDataSet
import kotlinx.coroutines.coroutineScope

/**
 * https://github.com/vsch/flexmark-java/wiki/Extensions#html-to-markdown
 */
object Html2MdConverter {
    private val settings =
        MutableDataSet()
            .set(FlexmarkHtmlConverter.EXT_INLINE_IMAGE, LinkConversion.NONE)
            .set(FlexmarkHtmlConverter.EXT_INLINE_LINK, LinkConversion.NONE)
            .toImmutable()

    private val converter = FlexmarkHtmlConverter.builder(settings).build()

    suspend fun convertHtmlToMarkdown(html: String): String =
        coroutineScope {
            converter.convert(html, -1)
        }
}
