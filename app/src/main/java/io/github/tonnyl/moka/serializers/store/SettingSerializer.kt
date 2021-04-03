package io.github.tonnyl.moka.serializers.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import io.github.tonnyl.moka.proto.Settings
import java.io.InputStream
import java.io.OutputStream

object SettingSerializer : Serializer<Settings> {

    override val defaultValue: Settings
        get() = Settings.getDefaultInstance()
            .toBuilder()
            .apply {
                theme = Settings.Theme.AUTO
                enableNotifications = true
                notificationSyncInterval = Settings.NotificationSyncInterval.ONE_QUARTER
                dnd = true
                doNotKeepSearchHistory = true
                keepData = Settings.KeepData.FOREVER
                autoSave = true
            }
            .build()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        t.writeTo(output)
    }

}