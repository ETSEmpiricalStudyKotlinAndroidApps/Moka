package io.github.tonnyl.moka.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventRepository(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: String,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("full_name")
    @ColumnInfo(name = "full_name")
    var fullName: String?,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String,

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String?

) : Parcelable