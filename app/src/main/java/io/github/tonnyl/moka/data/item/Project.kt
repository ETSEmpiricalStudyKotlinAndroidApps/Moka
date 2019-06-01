package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import io.github.tonnyl.moka.fragment.Project as RawProject
import io.github.tonnyl.moka.type.ProjectState as RawProjectState

/**
 * Projects manage issues, pull requests and notes within a project owner.
 */
@Parcelize
data class Project(

        /**
         * The project's description body.
         */
        val body: String?,

        /**
         * The projects description body rendered to HTML.
         */
        val bodyHTML: String,

        /**
         * true if the object is closed (definition of closed may depend on type).
         */
        val closed: Boolean,

        /**
         * Identifies the date and time when the object was closed.
         */
        val closedAt: Date?,

        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,

        /**
         * The actor who originally created the project.
         */
        val creator: ProjectActor?,

        /**
         * Identifies the primary key from the database.
         */
        val databaseId: Int?,

        val id: String,

        /**
         * The project's name.
         */
        val name: String,

        /**
         * The project's number.
         */
        val number: Int,

        /**
         * The HTTP path for this project.
         */
        val resourcePath: Uri,

        /**
         * Whether the project is open or closed.
         */
        val state: ProjectState,

        /**
         * Identifies the date and time when the object was last updated.
         */
        val updatedAt: Date,

        /**
         * The HTTP URL for this project.
         */
        val url: Uri,

        /**
         * Check if the current viewer can update this object.
         */
        val viewerCanUpdate: Boolean

) : Parcelable {

    companion object {

        fun createFromRaw(data: RawProject): Project = Project(
                data.body(),
                data.bodyHTML(),
                data.closed(),
                data.closedAt(),
                data.createdAt(),
                ProjectActor.createFromRaw(data.creator()),
                data.databaseId(),
                data.id(),
                data.name(),
                data.number(),
                data.resourcePath(),
                ProjectState.createFromRaw(data.state()),
                data.updatedAt(),
                data.url(),
                data.viewerCanUpdate()
        )

    }

}

/**
 * Represents an object which can take actions on GitHub. Typically a User or Bot.
 */
@Parcelize
data class ProjectActor(

        /**
         * A URL pointing to the actor's public avatar.
         */
        val avatarUrl: Uri,

        /**
         * The username of the actor.
         */
        val login: String,

        /**
         * The HTTP path for this actor.
         */
        val resourcePath: Uri,

        /**
         * The HTTP URL for this actor.
         */
        val url: Uri

) : Parcelable {

    companion object {

        fun createFromRaw(data: RawProject.Creator?): ProjectActor? = if (data == null) {
            null
        } else {
            ProjectActor(
                    data.avatarUrl(),
                    data.login(),
                    data.resourcePath(),
                    data.url()
            )
        }

    }

}

/**
 * State of the project; either 'open' or 'closed'.
 */
enum class ProjectState {

    /**
     * The project is closed.
     */
    CLOSED,

    /**
     * The project is open.
     */
    OPEN;

    companion object {

        fun createFromRaw(data: RawProjectState): ProjectState = when (data) {
            RawProjectState.OPEN -> OPEN
            // including RawProjectState.CLOSED, RawProjectState.`$UNKNOWN`
            else -> CLOSED
        }

    }

}