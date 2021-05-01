package io.github.tonnyl.moka.serializers.store.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@ExperimentalSerializationApi
@Serializable
data class Emoji(

    @ProtoNumber(1)
    val emoji: String = "",

    @ProtoNumber(2)
    val names: List<String> = emptyList(),

    @ProtoNumber(3)
    val tags: List<String> = emptyList(),

    @ProtoNumber(4)
    val description: String = "",

    @ProtoNumber(5)
    val category: String = ""

)

@ExperimentalSerializationApi
@Serializable
data class RecentEmojis(

    val recentEmojis: List<Emoji> = emptyList()

)