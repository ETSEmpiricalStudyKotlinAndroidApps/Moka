package io.tonnyl.moka.common.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import io.tonnyl.moka.common.data.SignedInAccounts
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

object AccountSerializer : Serializer<SignedInAccounts> {

    override val defaultValue: SignedInAccounts
        get() = SignedInAccounts()

    override suspend fun readFrom(input: InputStream): SignedInAccounts {
        try {
            return ProtoBuf.decodeFromByteArray(input.readBytes())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: SignedInAccounts, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }

}