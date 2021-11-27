package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tonnyl.moka.common.data.Event.Companion.CREATE_EVENT
import kotlinx.datetime.Instant
import io.tonnyl.moka.common.data.Event as SerializableEvent

@Entity(tableName = "event")
data class Event(

    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "type")
    var type: String,

    // note the difference of serialized name, column name and field name
    @ColumnInfo(name = "is_public")
    var isPublic: Boolean,

    @Embedded(prefix = "actor_")
    var actor: EventActor,

    @Embedded(prefix = "repo_")
    var repo: EventRepository? = null,

    @Embedded(prefix = "org_")
    var org: EventOrg? = null,

    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

    @Embedded(prefix = "payload_")
    var payload: EventPayload? = null,

    /**
     * Only for [CREATE_EVENT].
     *
     * The git ref (or null if only a repository was created).
     */
    @ColumnInfo(name = "ref")
    var ref: String? = null,

    /**
     * Only for [CREATE_EVENT].
     *
     * The object that was created. Can be one of repository, branch, or tag
     */
    @ColumnInfo(name = "ref_type")
    var refType: String? = null,

    /**
     * Only for [CREATE_EVENT].
     *
     * The name of the repository's default branch (usually master).
     */
    @ColumnInfo(name = "master_branch")
    var masterBranch: String? = null,

    /**
     * Only for [CREATE_EVENT].
     *
     * The repository's current description.
     */
    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "pusher_type")
    var pusherType: String? = null,

    @ColumnInfo(name = "head")
    var head: String? = null,

    @ColumnInfo(name = "before")
    var before: String? = null

)

val SerializableEvent.dbModel: Event
    get() = Event(
        id = id,
        type = type,
        isPublic = isPublic,
        actor = actor.dbModel,
        repo = repo?.dbModel,
        org = org?.dbModel,
        createdAt = createdAt,
        payload = payload?.dbModel,
        ref = ref,
        refType = refType,
        masterBranch = masterBranch,
        description = description,
        pusherType = pusherType,
        head = head,
        before = before
    )