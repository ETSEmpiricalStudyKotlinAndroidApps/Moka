package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Represents an actor in a Git commit (ie. an author or committer).
 */
@Parcelize
data class GitActor(
    /**
     * A URL pointing to the author's public avatar.
     */
    val avatarUrl: Uri,

    /**
     * The timestamp of the Git action (authoring or committing).
     */
    val date: Date?,

    /**
     * The email in the Git commit.
     */
    val email: String?,

    /**
     * The name in the Git commit.
     */
    val name: String?,

    /**
     * The GitHub user corresponding to the email field. Null if no such user exists.
     */
    val user: User?

) : Parcelable