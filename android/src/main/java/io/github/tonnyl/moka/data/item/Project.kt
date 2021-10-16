package io.github.tonnyl.moka.data.item

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import io.tonnyl.moka.graphql.fragment.Actor as RawActor
import io.tonnyl.moka.graphql.fragment.Project as RawProject

/**
 * Projects manage issues, pull requests and notes within a project owner.
 */
@Entity(tableName = "project")
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
    var closedAt: Instant?,

    /**
     * Identifies the date and time when the object was created.
     */
    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

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
    var resourcePath: String,

    /**
     * Whether the project is open or closed.
     */
    @ColumnInfo(name = "state")
    var state: String,

    /**
     * Identifies the date and time when the object was last updated.
     */
    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant,

    /**
     * The HTTP URL for this project.
     */
    @ColumnInfo(name = "url")
    var url: String,

    /**
     * Check if the current viewer can update this object.
     */
    @ColumnInfo(name = "viewer_can_update")
    var viewerCanUpdate: Boolean

)

fun RawProject.toNonNullProject(): Project {
    return Project(
        body,
        bodyHTML,
        closed,
        closedAt,
        createdAt,
        creator?.actor?.toNonNullProjectActor(),
        id,
        name,
        number,
        resourcePath,
        state.rawValue,
        updatedAt,
        url,
        viewerCanUpdate
    )
}

/**
 * Represents an object which can take actions on GitHub. Typically a User or Bot.
 */
data class ProjectActor(

    /**
     * A URL pointing to the actor's public avatar.
     */
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    /**
     * The username of the actor.
     */
    @ColumnInfo(name = "login")
    var login: String,

    /**
     * The HTTP URL for this actor.
     */
    @ColumnInfo(name = "url")
    var url: String

)

fun RawActor.toNonNullProjectActor(): ProjectActor {
    return ProjectActor(avatarUrl, login, url)
}