package io.tonnyl.moka.common.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TrendingRepository(

    val author: String,

    val name: String,

    val avatar: String,

    val url: String,

    val description: String? = null,

    val language: String? = null,

    val languageColor: String? = null,

    val stars: Int,

    val forks: Int,

    val currentPeriodStars: Int,

    @Contextual
    val builtBy: List<TrendingRepositoryBuiltBy>?

)

@Serializable
data class TrendingRepositoryBuiltBy(

    val href: String,

    val avatar: String,

    val username: String

)