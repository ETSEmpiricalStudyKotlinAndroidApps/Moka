package io.github.tonnyl.moka.serializers.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import io.github.tonnyl.moka.proto.RecentEmojis
import java.io.InputStream
import java.io.OutputStream

object EmojiSerializer : Serializer<RecentEmojis> {

    override val defaultValue: RecentEmojis
        get() = RecentEmojis.getDefaultInstance()

    override fun readFrom(input: InputStream): RecentEmojis {
        try {
            return RecentEmojis.parseFrom(input)
        } catch (e: Exception) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override fun writeTo(t: RecentEmojis, output: OutputStream) {
        t.writeTo(output)
    }

}