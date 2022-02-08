package io.tonnyl.moka.common.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import io.tonnyl.moka.common.store.data.ExploreLanguage
import io.tonnyl.moka.common.store.data.ExploreOptions
import io.tonnyl.moka.common.store.data.ExploreSpokenLanguage
import io.tonnyl.moka.common.store.data.ExploreTimeSpan
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
                name = "All languages",
                color = "#ECECEC"
            ),
            exploreSpokenLanguage = ExploreSpokenLanguage(
                urlParam = "",
                name = "All languages"
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