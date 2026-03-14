@file:OptIn(ExperimentalSerializationApi::class)
package io.github.gmazzo.android.livewallpaper.weather.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializer(forClass = Date::class)
internal object DateSerializer : KSerializer<Date> {

    // it has to be computed, `SimpleDateFormat` is not thread safe
    private val dateFormat
        get()= SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    override fun serialize(encoder: Encoder, value: Date) =
        encoder.encodeString(dateFormat.format(value))

    override fun deserialize(decoder: Decoder): Date =
        dateFormat.parse(decoder.decodeString())!!

}
