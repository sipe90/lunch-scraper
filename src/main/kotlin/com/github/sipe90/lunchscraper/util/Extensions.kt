package com.github.sipe90.lunchscraper.util

import java.security.MessageDigest
import kotlin.reflect.full.memberProperties

@OptIn(ExperimentalStdlibApi::class)
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.toHexString()
}

fun List<String>.md5(): String = joinToString().md5()

fun <T : Any> T.toMap(): Map<String, Any?> =
    this::class
        .memberProperties
        .associate {
            it.name to
                it.getter
                    .call(this)
        }
