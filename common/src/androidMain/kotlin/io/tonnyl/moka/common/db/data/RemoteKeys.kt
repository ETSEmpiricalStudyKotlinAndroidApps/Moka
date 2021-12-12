package io.tonnyl.moka.common.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tonnyl.moka.common.db.data.RemoteKeys.Companion.EVENT_PREFIX

/**
 * [Event], [Notification] and [Project] shares this table to store the remote keys of pagination.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeys(

    /**
     * To make the [id] as primary key unique, insert a corresponding prefix for different data sources.
     *
     * For example, insert a [RemoteKeys] record of [Event] to database,
     * the id field should be `[EVENT_PREFIX]` + [Event.id], and the final result is like `event_id_of_event_bla_bla`.
     * Similarly, `notification_the_true_id_bla_bla` to [Notification], etc.
     */
    @PrimaryKey
    val id: String,

    val prev: String?,

    val next: String?

) {

    companion object {

        const val EVENT_PREFIX = "event_"
        const val NOTIFICATION_PREFIX = "notification_"
        const val PROJECT_PREFIX = "project_"

    }

}