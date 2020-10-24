package io.github.tonnyl.moka.data

import android.net.Uri
import io.github.tonnyl.moka.fragment.RepositoryTopic as RawRepositoryTopic

data class RepositoryTopic(

    val id: String,

    /**
     * The topic.
     */
    val topic: Topic,

    /**
     * The HTTP path for this repository-topic.
     */
    val resourcePath: Uri,

    /**
     * The HTTP URL for this repository-topic.
     */
    val url: Uri

)

fun RawRepositoryTopic.toNonNullRepositoryTopic(): RepositoryTopic {
    return RepositoryTopic(
        id,
        topic.fragments.topic.toNonNullTopic(),
        resourcePath,
        url
    )
}