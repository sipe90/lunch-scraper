package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.openai.OpenAIService
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.settings.SettingsService
import com.github.sipe90.lunchscraper.util.Utils
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.coroutineScope
import kotlinx.io.IOException
import kotlinx.serialization.json.JsonObject
import org.springframework.stereotype.Service

@Service
class ExtractionService(
    private val settingsService: SettingsService,
) {
    private val json = DefaultJson
    private val menuExtractionSchema =
        javaClass.getResourceAsStream("/openai/menu_extraction_schema.json")?.use {
            json.decodeFromString<JsonObject>(it.readAllBytes().decodeToString())
        } ?: throw IOException("Unable to read menu extraction JSON schema.")

    suspend fun extractMenusFromDocument(
        doc: String,
        hint: String?,
        params: Map<String, String> = emptyMap(),
    ): MenuExtractionResult =
        coroutineScope {
            val settings = settingsService.getSettings()
            val openAIService = OpenAIService(settings.openAi)

            val userPrompt = Utils.replacePlaceholders(settings.scrape.userPromptPrefix, params) + " ${hint ?: ""} $doc"

            val response =
                openAIService.createChatCompletion(
                    listOf(settings.scrape.systemPrompt),
                    listOf(userPrompt),
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
