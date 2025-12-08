package com.github.sipe90.lunchscraper.scraping.extraction

import com.github.sipe90.lunchscraper.openai.OpenAIService
import com.github.sipe90.lunchscraper.openapi.MenuExtractionResult
import com.github.sipe90.lunchscraper.scraping.ScrapeParameters
import com.github.sipe90.lunchscraper.util.Utils
import com.github.sipe90.lunchscraper.util.toMap
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.json.JsonObject

class OpenAIExtractionService(
    private val openAIService: OpenAIService,
) : ExtractionService {
    private val json = DefaultJson

    private val menuExtractionSchema =
        Utils.readFileToString("/openai/menu_extraction_schema.json").let { json.decodeFromString<JsonObject>(it) }
    private val systemPrompt =
        Utils.readFileToString("/openai/system_prompt.md")
    private val userPromptTemplate =
        Utils.readFileToString("/openai/user_prompt_template.md")

    override suspend fun extractMenusFromDocument(
        documents: List<String>,
        parameters: ScrapeParameters,
    ): MenuExtractionResult {
        val inlinedDocuments =
            documents
                .mapIndexed {
                    index,
                    document,
                    ->
                    "--- DOCUMENT ${index + 1} START ---\n${document}\n--- DOCUMENT ${index + 1} END ---"
                }.joinToString("\n")
        val userPromptTemplateWithDocuments = userPromptTemplate.replace("{{documents}}", inlinedDocuments)
        val userPrompt = Utils.replacePlaceholders(userPromptTemplateWithDocuments, parameters.toMap())

        val response =
            openAIService.createChatCompletion(
                listOf(systemPrompt),
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

        return json.decodeFromString(responseMessage)
    }
}
