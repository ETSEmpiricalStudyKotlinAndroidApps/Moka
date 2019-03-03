package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Gist(
        @SerializedName("id")
        val id: String,

        @SerializedName("html_url")
        val htmlUrl: String,

        @SerializedName("files")
        val files: Map<String, EventGistFile>,

        @SerializedName("public")
        val public: Boolean,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("updated_at")
        val updatedAt: Date,

        @SerializedName("description")
        val description: String,

        @SerializedName("comments")
        val comments: Int,

        @SerializedName("owner")
        val owner: EventActor,

        @SerializedName("truncated")
        val truncated: Boolean
) : Parcelable