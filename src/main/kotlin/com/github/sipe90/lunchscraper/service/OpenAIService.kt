package com.github.sipe90.lunchscraper.service

import com.github.sipe90.lunchscraper.config.LunchScraperConfiguration
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestSystemMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessageTextContentPartPart
import com.github.sipe90.lunchscraper.openai.model.CreateChatCompletionRequest
import com.github.sipe90.lunchscraper.openai.model.CreateChatCompletionRequestResponseFormat
import com.github.sipe90.lunchscraper.openai.model.CreateChatCompletionResponse
import com.github.sipe90.lunchscraper.openai.model.Model
import com.github.sipe90.lunchscraper.openai.model.ResponseFormatJsonSchemaJsonSchema
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.springframework.stereotype.Service

@Service
class OpenAIService(
    config: LunchScraperConfiguration,
) {
    private val model = Model.fromString(config.openAiConfig.model)

    private val httpClient: HttpClient =
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        explicitNulls = false
                        isLenient = true
                        allowSpecialFloatingPointValues = true
                        allowStructuredMapKeys = true
                        prettyPrint = false
                        useArrayPolymorphism = false
                    },
                )
            }

            expectSuccess = true

            defaultRequest {
                url(config.openAiConfig.baseUrl)
                bearerAuth(config.openAiConfig.apiKey)
            }
        }

    suspend fun createCompletion(
        systemMessages: List<String>,
        userMessages: List<String>,
        schemaOptions: SchemaOptions,
    ): CreateChatCompletionResponse =
        httpClient
            .post("chat/completions") {
                contentType(ContentType.Application.Json)
                setBody(createRequestModel(systemMessages, userMessages, schemaOptions))
            }.body()

    private fun createRequestModel(
        systemMessages: List<String>,
        userMessages: List<String>,
        schemaOptions: SchemaOptions,
    ) = CreateChatCompletionRequest(
        model = model,
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

    data class SchemaOptions(
        val name: String,
        val description: String,
        val schema: JsonObject,
    )
}
