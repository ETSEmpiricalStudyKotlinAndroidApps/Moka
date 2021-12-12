package io.tonnyl.moka.common.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import io.tonnyl.moka.common.store.data.RecentEmojis
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@ExperimentalSerializationApi
object EmojiSerializer : Serializer<RecentEmojis> {

    override val defaultValue: RecentEmojis
        get() = RecentEmojis()

    override suspend fun readFrom(input: InputStream): RecentEmojis {
        try {
            return ProtoBuf.decodeFromByteArray(input.readBytes())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: RecentEmojis, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }

}