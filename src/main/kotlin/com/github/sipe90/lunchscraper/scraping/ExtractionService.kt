package com.github.sipe90.lunchscraper.scraping

import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.openai.OpenAIService
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.coroutineScope
import kotlinx.io.IOException
import kotlinx.serialization.json.JsonObject
import org.springframework.stereotype.Service

@Service
class ExtractionService(
    config: LunchScraperConfiguration,
    private val openAIService: OpenAIService,
) {
    private val json = DefaultJson

    private val systemPrompt = config.scrapingConfig.systemPrompt
    private val userPromptPrefix = config.scrapingConfig.userPromptPrefix

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
            val userPrompt = replacePlaceholders(userPromptPrefix, params) + " ${hint ?: ""} $doc"
            val response =
                openAIService.createCompletion(
                    listOf(systemPrompt),
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

    private fun replacePlaceholders(
        prompt: String,
        params: Map<String, String>,
    ): String {
        val p = params.entries.fold(prompt) { p, (variable, value) -> p.replace("{{$variable}}", value) }
        if (p.contains(Regex.fromLiteral("{{\\w*}}"))) {
            throw IllegalArgumentException("Prompt contains undefined variables: $p")
        }
        return p
    }
}
