package com.github.sipe90.lunchscraper.serialization

import com.github.sipe90.lunchscraper.openai.model.Model
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
