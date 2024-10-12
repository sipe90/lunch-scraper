package com.github.sipe90.lunchscraper.openai

import com.github.sipe90.lunchscraper.domain.settings.OpenAiSettings
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestSystemMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessageTextContentPartPart
import com.github.sipe90.lunchscraper.openai.model.CreateChatCompletionRequest
import com.github.sipe90.lunchscraper.openai.model.CreateChatCompletionRequestResponseFormat
import com.github.sipe90.lunchscraper.openai.model.CreateChatCompletionResponse
import com.github.sipe90.lunchscraper.openai.model.ResponseFormatJsonSchemaJsonSchema
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

private val logger = KotlinLogging.logger {}

class OpenAIService(
    private val openAiSettings: OpenAiSettings,
) {
    private val json =
        Json {
            encodeDefaults = true
            explicitNulls = false
            isLenient = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = logger.isTraceEnabled()
            useArrayPolymorphism = false
            ignoreUnknownKeys = true
        }

    suspend fun createCompletion(
        systemMessages: List<String>,
        userMessages: List<String>,
        schemaOptions: SchemaOptions,
    ): CreateChatCompletionResponse =
        useClient {
            val createChatCompletionRequest = createRequestModel(systemMessages, userMessages, schemaOptions)
            logger.trace { "Sending chat completion request: ${json.encodeToString(createChatCompletionRequest) }" }

            val responseBody =
                it
                    .post("chat/completions") {
                        contentType(ContentType.Application.Json)
                        setBody(createChatCompletionRequest)
                    }.body<CreateChatCompletionResponse>()

            logger.trace { "Received response for chat completion request: ${json.encodeToString(responseBody) }" }

            responseBody
        }

    private fun createRequestModel(
        systemMessages: List<String>,
        userMessages: List<String>,
        schemaOptions: SchemaOptions,
    ) = CreateChatCompletionRequest(
        model = openAiSettings.model,
        messages =
            systemMessages.map {
                ChatCompletionRequestSystemMessage(
                    content = it,
                )
            } +
                userMessages.map {
                    ChatCompletionRequestUserMessage(
                        content =
                            listOf(
                                ChatCompletionRequestUserMessageTextContentPartPart(
                                    text = it,
                                ),
                            ),
                    )
                },
        temperature = 0.1F,
        responseFormat =
            CreateChatCompletionRequestResponseFormat(
                type = CreateChatCompletionRequestResponseFormat.Type.JSON_SCHEMA,
                jsonSchema =
                    ResponseFormatJsonSchemaJsonSchema(
                        name = schemaOptions.name,
                        description = schemaOptions.description,
                        schema = schemaOptions.schema,
                        strict = true,
                    ),
            ),
    )

    private suspend fun <T> useClient(block: suspend (httpClient: HttpClient) -> T) =
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
            }

            install(ContentNegotiation) {
                json(json)
            }

            expectSuccess = true

            defaultRequest {
                url(openAiSettings.baseUrl)
                bearerAuth(openAiSettings.apiKey)
            }
        }.use { block(it) }

    data class SchemaOptions(
        val name: String,
        val description: String,
        val schema: JsonObject,
    )
}
