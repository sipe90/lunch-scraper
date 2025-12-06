package com.github.sipe90.lunchscraper.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.JsonSchema
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.github.sipe90.lunchscraper.config.OpenAiConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.annotations.Property
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

class OpenAIService(
    @Property("lunch-scraper.open-ai")
    private val config: OpenAiConfig,
    @Property("ktor.development")
    private val developmentMode: Boolean,
) {
    private val openAi =
        OpenAI(
            token = config.apiKey,
            host = OpenAIHost(baseUrl = config.baseUrl),
            timeout = Timeout(socket = 60.seconds),
            logging =
                LoggingConfig(
                    logLevel = if (developmentMode) LogLevel.All else LogLevel.None,
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
                model = ModelId(config.model),
                messages = systemChatMessages + userChatMessages,
                responseFormat =
                    ChatResponseFormat.jsonSchema(
                        JsonSchema(
                            name = schemaOptions.name,
                            schema = schemaOptions.schema,
                        ),
                    ),
            )

        return runWithExponentialBackoff {
            openAi.chatCompletion(chatCompletionRequest)
        }
    }

    private suspend fun <T> runWithExponentialBackoff(
        maxRetries: Int = 5,
        initialDelayMs: Long = 500L,
        maxDelayMs: Long = 8_000L,
        run: suspend () -> T,
    ): T {
        var currentDelay = initialDelayMs

        repeat(maxRetries) { attempt ->
            try {
                return run()
            } catch (e: OpenAIException) {
                val cause = e.cause
                val status = (cause as? ClientRequestException)?.response?.status

                val isRateLimit = status == HttpStatusCode.TooManyRequests
                val isLastAttempt = attempt == maxRetries - 1

                if (!isRateLimit || isLastAttempt) {
                    throw e
                }

                logger.warn(e) { "Rate limited by OpenAI, retrying in ${currentDelay}ms (attempt ${attempt + 1}/$maxRetries)" }

                delay(currentDelay)

                currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMs)
            }
        }
        error("runWithExponentialBackoff reached an unreachable state")
    }

    data class SchemaOptions(
        val name: String,
        val schema: JsonObject,
    )
}
