package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Represents a Git commit.
 */
@Parcelize
data class Commit(
        /**
         * An abbreviated version of the Git object ID.
         */
        val abbreviatedOid: String,
        /**
         * The number of additions in this commit.
         */
        val additions: Int,
        /**
         * Authorship details of the commit.
         */
        val author: GitActor?,
        /**
         * Check if the committer and the author match.
         */
        val authoredByCommitter: Boolean,
        /**
         * The datetime when this commit was authored.
         */
        val authoredDate: Date,
        /**
         * The number of changed files in this commit.
         */
        val changedFiles: Int,
        /**
         * The HTTP path for this Git object.
         */
        val commitResourcePath: Uri,
        /**
         * The HTTP URL for this Git object.
         */
        val commitUrl: Uri,
        /**
         * The datetime when this commit was committed.
         */
        val committedDate: Date,
        /**
         * Check if committed via GitHub web UI.
         */
        val committedViaWeb: Boolean,
        /**
         * Committership details of the commit.
         */
        val committer: GitActor?,
        /**
         * The number of deletions in this commit.
         */
        val deletions: Int,
        val id: Int,
        /**
         * The Git commit message.
         */
        val message: String,
        /**
         * The Git commit message body.
         */
        val messageBody: String,
        /**
         * The commit message body rendered to HTML.
         */
        val messageBodyHTML: String,
        /**
         * The Git commit message headline.
         */
        val messageHeadline: String,
        /**
         * The commit message headline rendered to HTML.
         */
        val messageHeadlineHTML: String,
        /**
         * The Git object ID.
         */
        val oid: String,
        /**
         * The datetime when this commit was pushed.
         */
        val pushedDate: Date?,
        /**
         * The HTTP path for this commit.
         */
        val resourcePath: Uri,
        /**
         * Commit signing information, if present.
         */
        val signature: GitSignature?,
        /**
         * Returns a URL to download a tarball archive for a repository. Note: For private repositories, these links are temporary and expire after five minutes.
         */
        val tarballUrl: Uri,
        /**
         * The HTTP URL for the tree of this commit.
         */
        val treeUrl: Uri,
        /**
         * The HTTP URL for this commit.
         */
        val url: Uri,
        /**
         * Check if the viewer is able to change their subscription status for the repository.
         */
        val viewerCanSubscribe: Boolean,
        /**
         * Identifies if the viewer is watching, not watching, or ignoring the subscribable entity.
         */
        val viewerSubscription: SubscriptionState?,
        /**
         * Returns a URL to download a zipball archive for a repository. Note: For private repositories, these links are temporary and expire after five minutes.
         */
        val zipballUrl: Uri
) : Parcelable