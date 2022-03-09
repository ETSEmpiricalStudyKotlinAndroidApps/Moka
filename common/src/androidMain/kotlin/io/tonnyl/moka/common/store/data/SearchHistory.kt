package io.tonnyl.moka.common.store.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class SearchHistory(

    @ProtoNumber(1)
    val queries: List<Query> = emptyList()

)

@Serializable
data class Query(

    @ProtoNumber(1)
    val keyword: String = "",

    @ProtoNumber(2)
    val queryTime: Instant = Instant.DISTANT_PAST

)