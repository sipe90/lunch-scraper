package com.github.sipe90.lunchscraper.scraping.loader

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

object HtmlDocumentCleaner {
    fun cleanDocument(doc: Document): String {
        val body = doc.body() ?: return ""

        // Remove script and style elements to reduce clutter
        body.select("meta, script, style, noscript, footer, nav").remove()

        // We'll build the final text representation here
        val sb = StringBuilder()

        // Start from the top-level children in the body
        body.children().forEach {
            processElement(it, sb)
        }

        return sb.toString().trim()
    }

    // Recursive function to convert each element into minimal Markdown
    private fun processElement(
        element: Element,
        sb: StringBuilder,
    ) {
        when (element.tagName().lowercase()) {
            "h1" -> {
                sb
                    .append("# ")
                    .append(element.ownText().trim())
                    .append("\n\n")
            }
            "h2" -> {
                sb
                    .append("## ")
                    .append(element.ownText().trim())
                    .append("\n\n")
            }
            "h3" -> {
                sb
                    .append("### ")
                    .append(element.ownText().trim())
                    .append("\n\n")
            }
            "h4" -> {
                sb
                    .append("#### ")
                    .append(element.ownText().trim())
                    .append("\n\n")
            }
            "li" -> {
                // List item
                val text = element.ownText().trim()
                if (text.isNotEmpty()) {
                    sb
                        .append("- ")
                        .append(text)
                        .append("\n")
                }
            }
            "br" -> {
                // Line break
                sb.append("\n")
            }
            "p", "div" -> {
                // Treat paragraphs and generic divs similarly, but separate them with blank lines
                val text = element.ownText().trim()
                if (text.isNotEmpty()) {
                    sb
                        .append(text)
                        .append("\n\n")
                }
            }
            else -> {
                // Default: If this element has its own text, we handle it;
                // also, we recursively process child elements.
                val text = element.ownText().trim()
                if (text.isNotEmpty()) {
                    sb
                        .append(text)
                        .append("\n\n")
                }
            }
        }

        // Process child elements recursively
        element.children().forEach {
            processElement(it, sb)
        }
    }
}
