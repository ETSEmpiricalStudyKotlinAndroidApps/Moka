package io.tonnyl.moka.common.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import io.tonnyl.moka.common.store.data.KeepData
import io.tonnyl.moka.common.store.data.NotificationSyncInterval
import io.tonnyl.moka.common.store.data.Settings
import io.tonnyl.moka.common.store.data.Theme
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

object SettingSerializer : Serializer<Settings> {

    override val defaultValue: Settings
        get() = Settings(
            theme = Theme.AUTO,
            enableNotifications = true,
            notificationSyncInterval = NotificationSyncInterval.ONE_QUARTER,
            dnd = true,
            doNotKeepSearchHistory = true,
            keepData = KeepData.FOREVER,
            autoSave = true
        )

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return ProtoBuf.decodeFromByteArray(input.readBytes())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(t))
    }

}