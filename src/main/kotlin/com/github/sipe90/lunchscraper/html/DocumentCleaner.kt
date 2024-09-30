package com.github.sipe90.lunchscraper.html

import org.jsoup.internal.StringUtil
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor

object DocumentCleaner {
    fun cleanDocument(doc: Document): String {
        doc.outputSettings().prettyPrint(false)

        val tagsToRemove = listOf("meta", "script", "style", "footer", "nav")
        val nodesToRemove = mutableListOf<Element>()

        NodeTraversor.traverse(
            object : NodeVisitor {
                override fun head(
                    node: Node,
                    depth: Int,
                ) {
                    when (node) {
                        is Comment -> node.remove()
                        is TextNode -> node.text(trim(node.text()))
                        is Element ->
                            if (tagsToRemove.contains(node.tagName())) {
                                node.remove()
                            } else {
                                node.clearAttributes()
                            }
                    }
                }

                override fun tail(
                    node: Node,
                    depth: Int,
                ) {
                    if (node is Element) {
                        val nodeText = trim(node.text())
                        if (StringUtil.isBlank(nodeText)) {
                            nodesToRemove.add(node)
                        }
                    }
                }
            },
            doc.body(),
        )

        nodesToRemove.forEach { it.remove() }

        return doc.body().outerHtml()
    }

    private fun trim(text: String) = text.replace("\u00A0", "").trim()
}
