package io.github.tonnyl.moka.db.converter

import android.net.Uri
import androidx.room.TypeConverter

object UriConverters {

    @TypeConverter
    @JvmStatic
    fun uriToString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    @JvmStatic
    fun fromString(uriString: String?): Uri? = uriString?.let {
        Uri.parse(it)
    }

}