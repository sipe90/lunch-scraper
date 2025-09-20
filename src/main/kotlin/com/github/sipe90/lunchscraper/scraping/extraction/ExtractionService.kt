package com.github.sipe90.lunchscraper.scraping.extraction

import com.github.sipe90.lunchscraper.openai.OpenAIService
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.coroutineScope
import kotlinx.io.IOException
import kotlinx.serialization.json.JsonObject

class ExtractionService(
    private val openAIService: OpenAIService,
) {
    private val json = DefaultJson
    private val menuExtractionSchema =
        javaClass.getResourceAsStream("/openai/menu_extraction_schema.json")?.use {
            json.decodeFromString<JsonObject>(it.readAllBytes().decodeToString())
        } ?: throw IOException("Unable to read menu extraction JSON schema.")

    suspend fun extractMenusFromDocument(
        systemMessage: String,
        userMessage: String,
    ): MenuExtractionResult =
        coroutineScope {
            val response =
                openAIService.createChatCompletion(
                    listOf(systemMessage),
                    listOf(userMessage),
                    OpenAIService.SchemaOptions(
                        name = "weeks_lunch_menus",
                        schema = menuExtractionSchema,
                    ),
                )

            val responseMessage =
                response.choices
                    .first()
                    .message.content!!

            json.decodeFromString(responseMessage)
        }
}
