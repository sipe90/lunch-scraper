package com.github.sipe90.lunchscraper.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.JsonSchema
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.github.sipe90.lunchscraper.domain.settings.OpenAiSettings
import kotlinx.serialization.json.JsonObject
import kotlin.time.Duration.Companion.seconds

class OpenAIService(
    private val openAiSettings: OpenAiSettings,
) {
    private val openAi =
        OpenAI(
            token = openAiSettings.apiKey,
            host = OpenAIHost(baseUrl = openAiSettings.baseUrl),
            timeout = Timeout(socket = 60.seconds),
            logging =
                LoggingConfig(
                    logLevel = LogLevel.All,
                    logger = Logger.Default,
                ),
        )

    suspend fun createChatCompletion(
        systemMessages: List<String>,
        userMessages: List<String>,
        schemaOptions: SchemaOptions,
    ): ChatCompletion {
        val systemChatMessages =
            systemMessages.map {
                ChatMessage(
                    role = ChatRole.System,
                    content = it,
                )
            }

        val userChatMessages =
            userMessages.map {
                ChatMessage(
                    role = ChatRole.User,
                    content = it,
                )
            }

        val chatCompletionRequest =
            ChatCompletionRequest(
                model = ModelId(openAiSettings.model),
                messages = systemChatMessages + userChatMessages,
                responseFormat =
                    ChatResponseFormat.jsonSchema(
                        JsonSchema(
                            name = schemaOptions.name,
                            schema = schemaOptions.schema,
                        ),
                    ),
            )

        return openAi.chatCompletion(chatCompletionRequest)
    }

    data class SchemaOptions(
        val name: String,
        val schema: JsonObject,
    )
}
