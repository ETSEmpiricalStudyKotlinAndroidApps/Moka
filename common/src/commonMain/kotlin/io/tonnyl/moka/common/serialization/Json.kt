package io.tonnyl.moka.common.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus

val json by lazy {
    Json {
        encodeDefaults = true
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule += SerializersModule {
            contextual(InstantSerializer)
        }
    }
}