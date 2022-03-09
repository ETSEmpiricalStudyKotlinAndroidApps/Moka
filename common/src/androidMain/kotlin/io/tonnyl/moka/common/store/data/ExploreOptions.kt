package io.tonnyl.moka.common.store.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class ExploreOptions(

    @ProtoNumber(0)
    val timeSpan: ExploreTimeSpan,

    @ProtoNumber(1)
    val exploreLanguage: ExploreLanguage,

    @ProtoNumber(2)
    val exploreSpokenLanguage: ExploreSpokenLanguage = ExploreSpokenLanguage(
        urlParam = "",
        name = "All languages"
    )

)

@Serializable
enum class ExploreTimeSpan {

    @ProtoNumber(0)
    DAILY,

    @ProtoNumber(1)
    WEEKLY,

    @ProtoNumber(2)
    MONTHLY;

}

@Serializable
data class ExploreLanguage(

    @ProtoNumber(1)
    val urlParam: String,

    @ProtoNumber(2)
    val name: String,

    @ProtoNumber(3)
    val color: String

)

@Serializable
data class ExploreSpokenLanguage(

    @ProtoNumber(1)
    val urlParam: String,

    @ProtoNumber(2)
    val name: String

)

val ExploreTimeSpan.urlParamValue: String
    get() = when (this) {
        ExploreTimeSpan.DAILY -> {
            "daily"
        }
        ExploreTimeSpan.WEEKLY -> {
            "weekly"
        }
        ExploreTimeSpan.MONTHLY -> {
            "monthly"
        }
    }