package com.github.sipe90.lunchscraper.serialization

import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestAssistantMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestFunctionMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestSystemMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestToolMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessage
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ChatCompletionRequestMessageSerializer : JsonContentPolymorphicSerializer<ChatCompletionRequestMessage>(
    ChatCompletionRequestMessage::class,
) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ChatCompletionRequestMessage> =
        when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "system" -> ChatCompletionRequestSystemMessage.serializer()
            "user" -> ChatCompletionRequestUserMessage.serializer()
            "assistant" -> ChatCompletionRequestAssistantMessage.serializer()
            "tool" -> ChatCompletionRequestToolMessage.serializer()
            "function" -> ChatCompletionRequestFunctionMessage.serializer()
            else -> throw IllegalArgumentException()
        }
}
