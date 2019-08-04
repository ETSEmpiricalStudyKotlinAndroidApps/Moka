package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*
import io.github.tonnyl.moka.fragment.Project as RawProject
import io.github.tonnyl.moka.type.ProjectState as RawProjectState

/**
 * Projects manage issues, pull requests and notes within a project owner.
 */
@Entity(tableName = "project")
@Parcelize
data class Project(

    /**
     * The project's description body.
     */
    @ColumnInfo(name = "body")
    var body: String?,

    /**
     * The projects description body rendered to HTML.
     */
    @ColumnInfo(name = "body_HTML")
    var bodyHTML: String,

    /**
     * true if the object is closed (definition of closed may depend on type).
     */
    @ColumnInfo(name = "closed")
    var closed: Boolean,

    /**
     * Identifies the date and time when the object was closed.
     */
    @ColumnInfo(name = "closed_at")
    var closedAt: Date?,

    /**
     * Identifies the date and time when the object was created.
     */
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    /**
     * The actor who originally created the project.
     */
    @Embedded(prefix = "project_creator")
    var creator: ProjectActor?,

    /**
     * Identifies the primary key from the database.
     */
    @ColumnInfo(name = "databaseId")
    var databaseId: Int?,

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,

    /**
     * The project's name.
     */
    @ColumnInfo(name = "name")
    var name: String,

    /**
     * The project's number.
     */
    @ColumnInfo(name = "number")
    var number: Int,

    /**
     * The HTTP path for this project.
     */
    @ColumnInfo(name = "resource_path")
    var resourcePath: Uri,

    /**
     * Whether the project is open or closed.
     */
    @ColumnInfo(name = "state")
    var state: ProjectState,

    /**
     * Identifies the date and time when the object was last updated.
     */
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    /**
     * The HTTP URL for this project.
     */
    @ColumnInfo(name = "url")
    var url: Uri,

    /**
     * Check if the current viewer can update this object.
     */
    @ColumnInfo(name = "viewer_can_update")
    var viewerCanUpdate: Boolean

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
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: Uri,

    /**
     * The username of the actor.
     */
    @ColumnInfo(name = "login")
    var login: String,

    /**
     * The HTTP path for this actor.
     */
    @ColumnInfo(name = "resource_path")
    var resourcePath: Uri,

    /**
     * The HTTP URL for this actor.
     */
    @ColumnInfo(name = "url")
    var url: Uri

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