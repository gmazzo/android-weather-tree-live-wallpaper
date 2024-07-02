package io.github.gmazzo.android.livewallpaper.weather

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateSerializer : KSerializer<Date> {

    private val dateFormat = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    }

    override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Date) =
        encoder.encodeString(dateFormat.get()!!.format(value))

    override fun deserialize(decoder: Decoder): Date =
        dateFormat.get()!!.parse(decoder.decodeString())!!

}
