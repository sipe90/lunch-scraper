package com.github.sipe90.lunchscraper.serialization

import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessageContentPart
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessageTextContentPartPart
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessageUrlContentPartPart
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ChatCompletionRequestUserMessageContentPartSerializer :
    JsonContentPolymorphicSerializer<ChatCompletionRequestUserMessageContentPart>(
        ChatCompletionRequestUserMessageContentPart::class,
    ) {
    override fun selectDeserializer(element: JsonElement) =
        when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "text" -> ChatCompletionRequestUserMessageTextContentPartPart.serializer()
            "image_url" -> ChatCompletionRequestUserMessageUrlContentPartPart.serializer()
            else -> throw IllegalArgumentException()
        }
}
