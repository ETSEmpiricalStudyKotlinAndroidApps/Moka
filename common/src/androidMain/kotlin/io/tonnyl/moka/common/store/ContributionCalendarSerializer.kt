package io.tonnyl.moka.common.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import io.tonnyl.moka.common.store.data.ContributionCalendar
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

object ContributionCalendarSerializer : Serializer<ContributionCalendar> {

    override val defaultValue: ContributionCalendar
        get() = ContributionCalendar()

    override suspend fun readFrom(input: InputStream): ContributionCalendar {
        try {
            return ProtoBuf.decodeFromByteArray(input.readBytes())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: ContributionCalendar, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }

}