package io.github.tonnyl.moka.db.converter

import android.net.Uri
import androidx.room.TypeConverter
import io.github.tonnyl.moka.util.MoshiInstance

object UriConverters {

    @TypeConverter
    @JvmStatic
    fun uriToString(uri: Uri?): String? {
        return uri?.let {
            MoshiInstance.uriAdapter.toJson(it)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromString(uriString: String?): Uri? {
        return uriString?.let {
            MoshiInstance.uriAdapter.fromJson(it)
        }
    }

}