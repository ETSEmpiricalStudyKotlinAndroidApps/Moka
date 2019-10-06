package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.tonnyl.moka.type.ProjectState
import kotlinx.android.parcel.Parcelize
import java.util.*
import io.github.tonnyl.moka.fragment.Actor as RawActor
import io.github.tonnyl.moka.fragment.Project as RawProject

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

) : Parcelable

fun RawProject.toNonNullProject(): Project {
    return Project(
        body(),
        bodyHTML(),
        closed(),
        closedAt(),
        createdAt(),
        creator()?.fragments()?.actor()?.toNonNullProjectActor(),
        id(),
        name(),
        number(),
        resourcePath(),
        state(),
        updatedAt(),
        url(),
        viewerCanUpdate()
    )
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
     * The HTTP URL for this actor.
     */
    @ColumnInfo(name = "url")
    var url: Uri

) : Parcelable

fun RawActor.toNonNullProjectActor(): ProjectActor {
    return ProjectActor(avatarUrl(), login(), url())
}