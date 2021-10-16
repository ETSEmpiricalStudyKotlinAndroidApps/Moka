package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.RepositoryTopic as RawRepositoryTopic

data class RepositoryTopic(

    val id: String,

    /**
     * The topic.
     */
    val topic: Topic?,

    /**
     * The HTTP path for this repository-topic.
     */
    val resourcePath: String,

    /**
     * The HTTP URL for this repository-topic.
     */
    val url: String

)

fun RawRepositoryTopic.toNonNullRepositoryTopic(): RepositoryTopic {
    return RepositoryTopic(
        id,
        topic.topic.toNonNullTopic(),
        resourcePath,
        url
    )
}