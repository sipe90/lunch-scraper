package com.github.sipe90.lunchscraper.serialization

import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestAssistantMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestFunctionMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestSystemMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestToolMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessage
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessageContentPart
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessageTextContentPartPart
import com.github.sipe90.lunchscraper.openai.model.ChatCompletionRequestUserMessageUrlContentPartPart
import com.github.sipe90.lunchscraper.openai.model.Model
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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

object ModelSerializer : KSerializer<Model> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Model", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Model,
    ) {
        val string = if (value.specifier != null) "${value.baseModel}-${value.specifier}" else value.baseModel
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): Model {
        val string = decoder.decodeString()
        return Model.fromString(string)
    }
}
