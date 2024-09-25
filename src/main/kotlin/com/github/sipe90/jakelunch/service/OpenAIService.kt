package com.github.sipe90.jakelunch.service

import com.github.sipe90.jakelunch.openai.model.CreateChatCompletionRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.springframework.stereotype.Service

@Service
class OpenAIService(
    private val httpClient: HttpClient,
) {
    suspend fun createCompletion() {
        httpClient.post("https://api.openai.com/v1/chat/completions") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateChatCompletionRequest(
                    model = CreateChatCompletionRequest.Model.GPT4oMINI,
                    messages =
                        listOf(
                            CreateChatCompletionRequest.ChatCompletionRequestSystemMessage(
                                content = "blablabla",
                                name = "system",
                            ),
                            CreateChatCompletionRequest.ChatCompletionRequestUserMessage(
                                content = listOf(),
                                name = "system",
                            ),
                        ),
                ),
            )
        }
    }
}
