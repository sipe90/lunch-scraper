package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.openai.OpenAIService
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.settings.GlobalSettingsService
import com.github.sipe90.lunchscraper.util.Utils
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.coroutineScope
import kotlinx.io.IOException
import kotlinx.serialization.json.JsonObject
import org.springframework.stereotype.Service

@Service
class ExtractionService(
    private val globalSettingsService: GlobalSettingsService,
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
            val settings = globalSettingsService.getGlobalSettings()
            val openAIService = OpenAIService(settings.openAi)

            val userPrompt = Utils.replacePlaceholders(settings.scrape.userPromptPrefix, params) + " ${hint ?: ""} $doc"

            val response =
                openAIService.createCompletion(
                    listOf(settings.scrape.systemPrompt),
                    listOf(userPrompt),
                    OpenAIService.SchemaOptions(
                        name = "weeks_lunch_menus",
                        description = "A restaurant's weekly lunch menus",
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
