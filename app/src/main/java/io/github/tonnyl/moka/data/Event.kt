package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Event(
        val id: String,
        val type: String,
        val public: Boolean,
        val actor: EventActor,
        val repo: EventRepo,
        val org: EventOrg?,
        @SerializedName("created_at")
        val createdAt: Date
) : Parcelable {

    companion object {
        const val WATCH_EVENT = "WatchEvent"
        const val FORK_EVENT = "ForkEvent"
        const val CREATE_EVENT = "CreateEvent"
    }

}