package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitResponse(

    val url: String,

    val sha: String,

    @SerialName("node_id")
    val nodeId: String,

    @SerialName("html_url")
    val htmlUrl: String,

    @SerialName("comments_url")
    val commentsUrl: String,

    @Contextual
    val commit: Commit,

    @Contextual
    val author: EventActor,

    @Contextual
    val committer: EventActor,

    @Contextual
    val parents: List<CommitParent>,

    @Contextual
    val stats: CommitStats,

    @Contextual
    val files: List<CommitFile>

)

@Serializable
data class Commit(

    val url: String,

    @Contextual
    val author: CommitCommitter,

    @Contextual
    val committer: CommitCommitter,

    val message: String,

    val tree: CommitTree,

    @SerialName("comment_count")
    val commentCount: Int,

    @Contextual
    val verification: CommitVerification?

)

@Serializable
data class CommitCommitter(

    val name: String,

    val email: String,

    @Contextual
    val date: Instant

)

@Serializable
data class CommitTree(

    val url: String,

    val sha: String

)

@Serializable
data class CommitVerification(

    val verified: Boolean,

    val reason: String,

    val signature: String? = null,

    val payload: String? = null

)

@Serializable
data class CommitParent(

    val url: String,

    val sha: String

)

@Serializable
data class CommitStats(

    val additions: Long,

    val deletions: Long,

    val total: Long

)

@Serializable
data class CommitFile(

    val filename: String,

    val additions: Long,

    val deletions: Long,

    val changes: Long,

    val status: String,

    @SerialName("raw_url")
    val rawUrl: String,

    @SerialName("blob_url")
    val blobUrl: String,

    val patch: String? = null

)