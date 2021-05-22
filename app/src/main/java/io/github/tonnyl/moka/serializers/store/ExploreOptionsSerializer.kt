package io.github.tonnyl.moka.serializers.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import io.github.tonnyl.moka.serializers.store.data.ExploreLanguage
import io.github.tonnyl.moka.serializers.store.data.ExploreOptions
import io.github.tonnyl.moka.serializers.store.data.ExploreTimeSpan
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@ExperimentalSerializationApi
object ExploreOptionsSerializer : Serializer<ExploreOptions> {

    override val defaultValue: ExploreOptions
        get() = ExploreOptions(
            timeSpan = ExploreTimeSpan.DAILY,
            exploreLanguage = ExploreLanguage(
                urlParam = "",
                name = "All Languages",
                color = "#ECECEC"
            )
        )

    override suspend fun readFrom(input: InputStream): ExploreOptions {
        try {
            return ProtoBuf.decodeFromByteArray(input.readBytes())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: ExploreOptions, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }
}