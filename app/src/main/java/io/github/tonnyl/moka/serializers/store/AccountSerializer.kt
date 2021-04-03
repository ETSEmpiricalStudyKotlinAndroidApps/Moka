package io.github.tonnyl.moka.serializers.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import io.github.tonnyl.moka.proto.SignedInAccounts
import java.io.InputStream
import java.io.OutputStream

object AccountSerializer : Serializer<SignedInAccounts> {

    override val defaultValue: SignedInAccounts
        get() = SignedInAccounts.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SignedInAccounts {
        try {
            return SignedInAccounts.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: SignedInAccounts, output: OutputStream) {
        t.writeTo(output)
    }

}