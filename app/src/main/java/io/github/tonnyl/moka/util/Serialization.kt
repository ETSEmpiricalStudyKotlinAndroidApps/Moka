package io.github.tonnyl.moka.util

import io.github.tonnyl.moka.serializers.serialization.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val json by lazy {
    Json {
        encodeDefaults = true
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(InstantSerializer)
        }
    }
}